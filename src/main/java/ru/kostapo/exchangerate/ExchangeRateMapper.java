package ru.kostapo.mappers;

import org.modelmapper.*;
import ru.kostapo.dto.CurrencyResDTO;
import ru.kostapo.dto.ExchangeRateReqDTO;
import ru.kostapo.dto.ExchangeRateResDTO;
import ru.kostapo.models.ExchangeRate;
import ru.kostapo.services.CurrencyService;
import ru.kostapo.services.CurrencyServiceImpl;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.List;

public class ExchangeRateMapper {

    private final CurrencyService currencyService;

    public ExchangeRateMapper(Connection connection) {
        this.currencyService = new CurrencyServiceImpl(connection);
    }

    public ExchangeRateResDTO toDTO(ExchangeRate exchangeRate) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new AbstractConverter<Integer, CurrencyResDTO>() {
            @Override
            protected CurrencyResDTO convert(Integer source) {
                return currencyService.findById(source).orElse(null);
            }
        });
        return modelMapper.map(exchangeRate, ExchangeRateResDTO.class);
    }

    public List<ExchangeRateResDTO> toDtoList(List<ExchangeRate> exchangeRates) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new AbstractConverter<Integer, CurrencyResDTO>() {
            @Override
            protected CurrencyResDTO convert(Integer source) {
                return currencyService.findById(source).orElse(null);
            }
        });
        Type listType = new TypeToken<List<ExchangeRateResDTO>>() {
        }.getType();
        return modelMapper.map(exchangeRates, listType);
    }

    public ExchangeRate toModel(ExchangeRateReqDTO exchangeRateReqDTO) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new AbstractConverter<String, Integer>() {
            @Override
            protected Integer convert(String source) {
                return getCurrencyIdByCode(source);
            }
        });
        modelMapper.addMappings(new PropertyMap<ExchangeRateReqDTO, ExchangeRate>() {
            @Override
            protected void configure() {
                source = exchangeRateReqDTO;
                skip().setId(null);
                map().setBaseCurrencyId(getCurrencyIdByCode(source.getBaseCurrencyCode()));
                map().setTargetCurrencyId(getCurrencyIdByCode(source.getTargetCurrencyCode()));
            }
        });
        return modelMapper.map(exchangeRateReqDTO, ExchangeRate.class);
    }

    private Integer getCurrencyIdByCode(String code) {
        CurrencyResDTO currencyResDTO = currencyService.findByCode(code)
                .orElseThrow(() -> new RuntimeException("CURRENCY " + code + " NOT FOUND"));
        return currencyResDTO.getId();
    }

}
