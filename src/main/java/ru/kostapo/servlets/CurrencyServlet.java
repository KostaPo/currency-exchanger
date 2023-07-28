package ru.kostapo.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kostapo.dto.CurrencyResDTO;
import ru.kostapo.dto.ExceptionResEDTO;
import ru.kostapo.exceptions.BadParameterException;
import ru.kostapo.exceptions.DatabaseException;
import ru.kostapo.services.CurrencyService;
import ru.kostapo.utils.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "CurrenciesServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {

    private CurrencyService currencyService;
    private ObjectMapper objectMapper;
    private StringUtils stringUtils;


    @Override
    public void init(ServletConfig config) {
        currencyService = (CurrencyService) config.getServletContext().getAttribute("currencyService");
        objectMapper = new ObjectMapper();
        stringUtils = new StringUtils();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String code = "null";
            try {
                String pathInfo = stringUtils.getPathInfo(request);
                code = pathInfo.replaceFirst("/", "").toUpperCase();
                stringUtils.isCodeValid(code);
            } catch (BadParameterException ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
            }
            Optional<CurrencyResDTO> currencyResponseDTO = currencyService.findByCode(code);
            if (currencyResponseDTO.isPresent()) {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getOutputStream(), currencyResponseDTO.get());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO("ВАЛЮТА "+code+" НЕ НАЙДЕНА"));
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String parameter = request.getParameter("id");
            try {
                Integer id = Integer.parseInt(parameter);
                currencyService.delete(id);
                response.setStatus(HttpServletResponse.SC_OK);
                response.sendRedirect(request.getContextPath() + "/currencies");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO("НЕ ВЕРНО УКАЗАН ID ВАЛЮТЫ"));
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
