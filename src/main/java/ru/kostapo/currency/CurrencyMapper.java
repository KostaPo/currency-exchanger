package ru.kostapo.mappers;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import ru.kostapo.dto.CurrencyReqDTO;
import ru.kostapo.dto.CurrencyResDTO;
import ru.kostapo.currency.Currency;

import java.lang.reflect.Type;
import java.util.List;

public class CurrencyMapper {

    public CurrencyResDTO toDTO(Currency currency) {
        ModelMapper modelMapper = getMapper();
        modelMapper.addMappings(new PropertyMap<Currency, CurrencyResDTO>() {
            protected void configure() {
                map().setName(source.getFullName());
            }
        });
        return modelMapper.map(currency, CurrencyResDTO.class);
    }

    public List<CurrencyResDTO> toDtoList(List<Currency> currencies) {
        ModelMapper modelMapper = getMapper();
        modelMapper.addMappings(new PropertyMap<Currency, CurrencyResDTO>() {
            protected void configure() {
                map().setName(source.getFullName());
            }
        });
        Type listType = new TypeToken<List<CurrencyResDTO>>() {
        }.getType();
        return modelMapper.map(currencies, listType);
    }

    public Currency toModel(CurrencyReqDTO currencyReqDTO) {
        ModelMapper modelMapper = getMapper();
        modelMapper.addMappings(new PropertyMap<CurrencyReqDTO, Currency>() {
            protected void configure() {
                skip().setId(null);
                map().setFullName(source.getName());
            }
        });
        return modelMapper.map(currencyReqDTO, Currency.class);
    }

    private ModelMapper getMapper() {
        return new ModelMapper();
    }
}
