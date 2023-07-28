package ru.kostapo.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kostapo.dto.ExceptionResEDTO;
import ru.kostapo.dto.ExchangeRateReqDTO;
import ru.kostapo.dto.ExchangeRateResDTO;
import ru.kostapo.exceptions.BadParameterException;
import ru.kostapo.exceptions.DatabaseException;
import ru.kostapo.exceptions.DublicationException;
import ru.kostapo.services.ExchangeRateService;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) {
        exchangeRateService = (ExchangeRateService) config.getServletContext().getAttribute("exchangeRateService");
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Optional<List<ExchangeRateResDTO>> rates = exchangeRateService.findAllExchangeRates();
            if (!rates.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO("ОБМЕННЫЙ КУРС НЕ НАЙДЕН"));
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getOutputStream(), rates.get());
            }
        } catch (DatabaseException ex) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            ExchangeRateReqDTO requestDTO = new ExchangeRateReqDTO(
                    request.getParameter("baseCurrencyCode"),
                    request.getParameter("targetCurrencyCode"),
                    new BigDecimal(request.getParameter("rate")));
            try {
                exchangeRateService.isRequestDataValid(requestDTO);
            } catch (BadParameterException ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
            }
            ExchangeRateResDTO responseDTO = exchangeRateService.save(requestDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getOutputStream(), responseDTO);
        } catch (DatabaseException ex) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        } catch (DublicationException ex) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        }
    }

}
