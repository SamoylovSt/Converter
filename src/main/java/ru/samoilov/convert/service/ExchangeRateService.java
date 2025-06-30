package ru.samoilov.convert.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.samoilov.convert.dao.ExchangeRateDAO;
import ru.samoilov.convert.dto.CurrencyDTO;
import ru.samoilov.convert.dto.ExchangeRateDTO;
import ru.samoilov.convert.dto.ExchangeResultDTO;
import ru.samoilov.convert.exception.BadRequestException;
import ru.samoilov.convert.exception.ConflictException;
import ru.samoilov.convert.exception.NotFountExchengeRateException;
import ru.samoilov.convert.models.ExchangeRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeRateService {

    private final ExchangeRateDAO exchangeRateDAO;

    public ExchangeRateService(ExchangeRateDAO exchangeRateDAO) {
        this.exchangeRateDAO = exchangeRateDAO;
    }

    public List<ExchangeRateDTO> showDefiniteExchangeRates(String baseCodeTargetCode) {

        String baseCode = baseCodeTargetCode.substring(0, 3);
        String targetCode = baseCodeTargetCode.substring(3);
        List<ExchangeRate> exchangeRates = exchangeRateDAO.showDefiniteExchangeRatesDAO(baseCode, targetCode);


        if (!exchangeRates.isEmpty()) {
            return getExchangeRateDTOList(exchangeRates);
        } else {
            throw new NotFountExchengeRateException("Обменный курс для пары не найден");
        }
    }

    public List<ExchangeRateDTO> showAllexchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDAO.showAllexchangeRates();
        return getExchangeRateDTOList(exchangeRates);
    }


    public List<ExchangeRateDTO> getExchangeRateDTOList(List<ExchangeRate> exchangeRates) {
        List<ExchangeRateDTO> exchangeRatesDTO = new ArrayList<>();
        for (ExchangeRate er : exchangeRates) {
            CurrencyDTO baseCurrencyDTO;
            CurrencyDTO targetCurrencyDTO;
            targetCurrencyDTO = exchangeRateDAO.findCurrencyForId(er.getTargetCurrencyId());
            baseCurrencyDTO = exchangeRateDAO.findCurrencyForId(er.getBaseCurrencyId());
            ExchangeRateDTO exchangeRateDTOTemp = new ExchangeRateDTO();
            exchangeRateDTOTemp.setId(er.getId());
            exchangeRateDTOTemp.setRate(er.getRate());
            exchangeRateDTOTemp.setBaseCurrency(baseCurrencyDTO);
            exchangeRateDTOTemp.setTargetCurrency(targetCurrencyDTO);
            exchangeRatesDTO.add(exchangeRateDTOTemp);
        }
        return exchangeRatesDTO;
    }

    public List<ExchangeRateDTO> saveExchangeRate(String baseCurrencyCode,
                                                         String targetCurrencyCode,
                                                         double rate) {
        try {
            if (exchangeRateDAO.exestByCodeBaseCurrency(baseCurrencyCode)) {
              //  return ResponseEntity.status(HttpStatus.CONFLICT).build();
                throw  new NotFountExchengeRateException("Валюта отстуствует ( сохранение валюты)");
            }
            List<ExchangeRate> resultDTO = new ArrayList<>();
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setBaseCurrencyId(exchangeRateDAO.getCurrencyIdForCode(baseCurrencyCode));
            exchangeRate.setTargetCurrencyId(exchangeRateDAO.getCurrencyIdForCode(targetCurrencyCode));
            exchangeRate.setRate(BigDecimal.valueOf(rate));
            exchangeRateDAO.exchangeRateSave(exchangeRate);
            resultDTO.add(exchangeRate);
            List<ExchangeRateDTO> resultList = getExchangeRateDTOList(resultDTO);
            //return ResponseEntity.ok(exchangeRate);
            return resultList;
        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            throw new BadRequestException("Сохранение обменного курса ошибка");
        }
    }

    public List<ExchangeResultDTO> exchange(String from, String to, int amount) {
        List<ExchangeResultDTO> exchangeResultDTOList = new ArrayList<>();
        ExchangeResultDTO exchangeResultDTO = new ExchangeResultDTO();
        List<ExchangeRate> currencyPairList = exchangeRateDAO.showDefiniteExchangeRatesDAO(from, to);
        List<ExchangeRate> currencyPairListReverse = exchangeRateDAO.showDefiniteExchangeRatesDAO(to, from);
        BigDecimal resultConvertedAmount = new BigDecimal(10);//= currencyPair.getRate().multiply(new BigDecimal(amount));
        if (!currencyPairList.isEmpty()) {
            ExchangeRate currencyPair = currencyPairList.get(0);
            resultConvertedAmount = currencyPair.getRate().multiply(new BigDecimal(amount));
            exchangeResultDTO.setRate(currencyPair.getRate());
        } else if (!currencyPairListReverse.isEmpty()) {
            ExchangeRate currencyPairReverse = currencyPairListReverse.get(0);
            exchangeResultDTO.setRate(currencyPairReverse.getRate());
            resultConvertedAmount = currencyPairReverse.getRate().divide(new BigDecimal(amount));
        }
        CurrencyDTO tempBaseCurrency = exchangeRateDAO.findCurrencyForId(exchangeRateDAO.getCurrencyIdForCode(from));
        CurrencyDTO tempTargetCurrency = exchangeRateDAO.findCurrencyForId(exchangeRateDAO.getCurrencyIdForCode(to));
        exchangeResultDTO.setBaseCurrency(tempBaseCurrency);
        exchangeResultDTO.setTargetCurrency(tempTargetCurrency);
        exchangeResultDTO.setAmount(amount);
        exchangeResultDTO.setConvertedAmount(resultConvertedAmount);
        exchangeResultDTOList.add(exchangeResultDTO);
        return exchangeResultDTOList;
    }

    public List<ExchangeRateDTO> changeRate(String baseCodeTargetCode, BigDecimal rate) {

        List<ExchangeRateDTO> currencyPairList = showDefiniteExchangeRates(baseCodeTargetCode);
        int id = 0;
        if (!currencyPairList.isEmpty()) {
            ExchangeRateDTO currencyPair = currencyPairList.get(0);
            id = currencyPair.getId();
        }
        exchangeRateDAO.changeRate(rate, id);
        return showDefiniteExchangeRates(baseCodeTargetCode);
    }
}

//public ResponseEntity<ExchangeRate> saveExchangeRate(String baseCurrencyCode,
//                                                     String targetCurrencyCode,
//                                                     double rate) {
//    try {
//        if (exchangeRateDAO.exestByCodeBaseCurrency(baseCurrencyCode)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        }
//        ExchangeRate exchangeRate = new ExchangeRate();
//        exchangeRate.setBaseCurrencyId(exchangeRateDAO.getCurrencyIdForCode(baseCurrencyCode));
//        exchangeRate.setTargetCurrencyId(exchangeRateDAO.getCurrencyIdForCode(targetCurrencyCode));
//        exchangeRate.setRate(BigDecimal.valueOf(rate));
//        exchangeRateDAO.exchangeRateSave(exchangeRate);
//        return ResponseEntity.ok(exchangeRate);
//    } catch (Exception e) {
//        e.printStackTrace();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//    }
//}