package ru.kostapo.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kostapo.dto.ExceptionResEDTO;
import ru.kostapo.dto.ExchangeRateReqDTO;
import ru.kostapo.dto.ExchangeReqDTO;
import ru.kostapo.dto.ExchangeResDTO;
import ru.kostapo.exceptions.BadParameterException;
import ru.kostapo.exceptions.DatabaseException;
import ru.kostapo.exceptions.NotFoundException;
import ru.kostapo.services.CurrencyService;
import ru.kostapo.services.ExchangeRateService;
import ru.kostapo.services.ExchangeService;
import ru.kostapo.utils.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {

    private ExchangeService exchangeService;
    private ObjectMapper objectMapper;
    private StringUtils stringUtils;

    @Override
    public void init(ServletConfig config) {
        exchangeService = (ExchangeService) config.getServletContext().getAttribute("exchangeService");
        objectMapper = new ObjectMapper();
        stringUtils = new StringUtils();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeReqDTO requestDTO = new ExchangeReqDTO();
        try {
            try {
                requestDTO.setFrom(request.getParameter("from"));
                requestDTO.setTo(request.getParameter("to"));
                requestDTO.setAmount(stringUtils.getBigDecimalByString(request.getParameter("amount")));
                exchangeService.isRequestDataValid(requestDTO);
            } catch (BadParameterException ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
            }
            try {
                Optional<ExchangeResDTO> responseDTO = exchangeService.getExchange(requestDTO);
                if (responseDTO.isPresent()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    objectMapper.writeValue(response.getOutputStream(), responseDTO.get());
                }
            } catch (NotFoundException ex) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
            }
        } catch (DatabaseException ex) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        }
    }
}
