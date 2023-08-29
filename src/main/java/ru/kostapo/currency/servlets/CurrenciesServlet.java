package ru.kostapo.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kostapo.currency.dto.CurrencyReqDTO;
import ru.kostapo.currency.dto.CurrencyResDTO;
import ru.kostapo.common.ExceptionResDTO;
import ru.kostapo.common.exceptions.BadParameterException;
import ru.kostapo.common.exceptions.DatabaseException;
import ru.kostapo.common.exceptions.DublicationException;
import ru.kostapo.currency.CurrencyService;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) {
        currencyService = (CurrencyService) config.getServletContext().getAttribute("currencyService");
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Optional<List<CurrencyResDTO>> currencies;
            currencies = currencyService.findAllCurrencies();
            if (!currencies.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResDTO("СПИСОК ВАЛЮТ ОТСУТСТВУЕТ В БД"));
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getOutputStream(), currencies.get());
            }
        } catch (DatabaseException ex) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResDTO(ex.getMessage()));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResDTO(ex.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CurrencyReqDTO requestDTO = new CurrencyReqDTO(
                    request.getParameter("code"),
                    request.getParameter("name"),
                    request.getParameter("sign"));
            try {
                currencyService.isRequestDataValid(requestDTO);
            } catch (BadParameterException ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResDTO(ex.getMessage()));
            }
            CurrencyResDTO responseDTO = currencyService.save(requestDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getOutputStream(), responseDTO);
        } catch (DatabaseException ex) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResDTO(ex.getMessage()));
        } catch (DublicationException ex) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResDTO(ex.getMessage()));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResDTO(ex.getMessage()));
        }
    }
}
