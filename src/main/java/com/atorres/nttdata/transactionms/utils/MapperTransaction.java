package com.atorres.nttdata.transactionms.utils;

import com.atorres.nttdata.transactionms.model.RequestTransaction;
import com.atorres.nttdata.transactionms.model.RequestTransactionAccount;
import com.atorres.nttdata.transactionms.model.TransactionDto;
import com.atorres.nttdata.transactionms.model.accountms.RequestUpdateAccount;
import com.atorres.nttdata.transactionms.model.dao.TransactionDao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class MapperTransaction {
    public TransactionDao retiroRequestToDao(RequestTransactionAccount requestTransactionAccount, BigDecimal balance){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom(requestTransactionAccount.getAccountId());
        transactionDao.setTo("CAJERO");
        transactionDao.setCategory("RETIRO");
        transactionDao.setDate(new Date());
        transactionDao.setComission(new BigDecimal("0.5"));
        transactionDao.setClientId(requestTransactionAccount.getClientId());
        return  transactionDao;
    }
    public TransactionDao depositoRequestToDao(RequestTransactionAccount request, BigDecimal balance){
        TransactionDao transactionDao = new TransactionDao();
        transactionDao.setBalance(balance);
        transactionDao.setFrom("CAJERO");
        transactionDao.setTo(request.getAccountId());
        transactionDao.setCategory("DEPOSITO");
        transactionDao.setDate(new Date());
        transactionDao.setComission(new BigDecimal("10.5"));
        transactionDao.setClientId(request.getClientId());
        return  transactionDao;
    }
    public RequestUpdateAccount toRequestUpdateAccount(BigDecimal balance, String from){
        RequestUpdateAccount request = new RequestUpdateAccount();
        request.setBalance(balance);
        request.setAccountId(from);
        return  request;
    }
    public TransactionDao transRequestToTransDao(RequestTransaction request, BigDecimal comision){
        TransactionDao trans = new TransactionDao();
        trans.setCategory("TRANSFERENCIA");
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
        return  trans;
    }
}
