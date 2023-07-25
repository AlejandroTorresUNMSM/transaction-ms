package com.atorres.nttdata.transactionms.utils;

import com.atorres.nttdata.transactionms.model.RequestTransaction;
import com.atorres.nttdata.transactionms.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionms.model.RequestRetiroDebit;
import com.atorres.nttdata.transactionms.model.TransactionDto;
import com.atorres.nttdata.transactionms.model.accountms.RequestUpdateAccount;
import com.atorres.nttdata.transactionms.model.dao.TransactionDao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class MapperTransaction {
    public static final String RETIRO = "RETIRO";
    public static final String DEPOSITO = "DEPOSITO";
    public static final String CAJERO = "CAJERO";
    public static final String TRANSFERENCIA = "TRANSFERENCIA";
    public static final String DEBITO = " DEBITO";
    public static final String CUENTA = " CUENTA";
    public TransactionDao retiroRequestToDao(RequestTransactionAccount requestTransactionAccount, BigDecimal balance,BigDecimal comision){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom(requestTransactionAccount.getAccountId());
        transactionDao.setTo(CAJERO);
        transactionDao.setCategory(RETIRO.concat(CUENTA));
        transactionDao.setDate(new Date());
        transactionDao.setComission(comision);
        transactionDao.setClientId(requestTransactionAccount.getClientId());
        return  transactionDao;
    }
    public TransactionDao depositoRequestToDao(RequestTransactionAccount request, BigDecimal balance,BigDecimal comision){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom(CAJERO);
        transactionDao.setTo(request.getAccountId());
        transactionDao.setCategory(DEPOSITO.concat(CUENTA));
        transactionDao.setDate(new Date());
        transactionDao.setComission(comision);
        transactionDao.setClientId(request.getClientId());
        return  transactionDao;
    }
    public RequestUpdateAccount toUpdateAccount(BigDecimal balance, String from){
        RequestUpdateAccount request = new RequestUpdateAccount();
        request.setBalance(balance);
        request.setAccountId(from);
        return  request;
    }
    public TransactionDao transRequestToTransDao(RequestTransaction request, BigDecimal comision){
        TransactionDao trans = new TransactionDao();
        trans.setCategory(TRANSFERENCIA.concat(CUENTA));
        trans.setFrom(request.getFrom());
        trans.setTo(request.getTo());
        trans.setBalance(request.getAmount());
        trans.setDate(new Date());
        trans.setComission(comision);
        trans.setClientId(request.getClientId());
        return  trans;
    }

    public TransactionDto toTransDto(TransactionDao transactionDao) {
        TransactionDto trans = new TransactionDto();
        trans.setId(transactionDao.getId());
        trans.setFrom(transactionDao.getFrom());
        trans.setTo(transactionDao.getTo());
        trans.setCategory(transactionDao.getCategory());
        trans.setBalance(transactionDao.getBalance());
        trans.setDate(transactionDao.getDate());
        trans.setClientId(transactionDao.getClientId());
        trans.setComission(transactionDao.getComission());
        return  trans;
    }

    public TransactionDao toTransDao(RequestRetiroDebit request, BigDecimal comision){
        TransactionDao trans = new TransactionDao();
        trans.setFrom(request.getDebit());
        trans.setTo(CAJERO);
        trans.setCategory(RETIRO.concat(DEBITO));
        trans.setBalance(request.getAmount());
        trans.setDate(new Date());
        trans.setClientId(request.getClientId());
        trans.setComission(comision);
        return trans;
    }
}
