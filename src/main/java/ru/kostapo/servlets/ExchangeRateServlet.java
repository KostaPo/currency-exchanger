package ru.kostapo.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kostapo.dto.ExceptionResEDTO;
import ru.kostapo.dto.ExchangeRateReqDTO;
import ru.kostapo.dto.ExchangeRateResDTO;
import ru.kostapo.exceptions.BadParameterException;
import ru.kostapo.exceptions.DatabaseException;
import ru.kostapo.exceptions.NotFoundException;
import ru.kostapo.services.ExchangeRateService;
import ru.kostapo.utils.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private StringUtils stringUtils;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) {
        exchangeRateService = (ExchangeRateService) config.getServletContext().getAttribute("exchangeRateService");
        objectMapper = new ObjectMapper();
        stringUtils = new StringUtils();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getMethod().equals("PATCH")) {
            super.service(req, resp);
        }
        this.doPatch(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String codePair = "null";
            try {
                String pathInfo = stringUtils.getPathInfo(request);
                codePair = pathInfo.replaceFirst("/", "").toUpperCase();
                stringUtils.isCodePairValid(codePair);
            } catch (BadParameterException ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
            }
            Optional<ExchangeRateResDTO> exchangeRateResDTO =
                    exchangeRateService.findByCodes(codePair.substring(0, 3), codePair.substring(3, 6));
            if (exchangeRateResDTO.isPresent()) {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getOutputStream(), exchangeRateResDTO.get());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(response.getOutputStream(),
                        new ExceptionResEDTO("ОБМЕННЫЙ КУРС ДЛЯ ПАРЫ " + codePair + " НЕ НАЙДЕН"));
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
                exchangeRateService.delete(id);
                response.setStatus(HttpServletResponse.SC_OK);
                response.sendRedirect(request.getContextPath() + "/exchangeRates");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO("НЕ ВЕРНО УКАЗАН ID ОБМЕННОГО КУРСА"));
            }
        } catch (DatabaseException ex) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String codePair = null;
            BigDecimal newRate = null;
            try {
                String pathInfo = stringUtils.getPathInfo(request);
                codePair = pathInfo.replaceFirst("/", "").toUpperCase();
                stringUtils.isCodePairValid(codePair);
                String rate = stringUtils.getValueByKey(request, "rate");
                newRate = stringUtils.getBigDecimalByString(rate);
            } catch (BadParameterException ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getOutputStream(), new ExceptionResEDTO(ex.getMessage()));
            }
            try {
                ExchangeRateReqDTO requestDTO = new ExchangeRateReqDTO(
                        codePair.substring(0, 3),
                        codePair.substring(3, 6),
                        newRate);
                ExchangeRateResDTO responseDTO = exchangeRateService.update(requestDTO);
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getOutputStream(), responseDTO);
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
