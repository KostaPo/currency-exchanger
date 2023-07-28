package ru.kostapo.listeners;

import ru.kostapo.services.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        ServletContext context = servletContextEvent.getServletContext();

        CurrencyService currencyService = new CurrencyServiceImpl();
        context.setAttribute("currencyService", currencyService);

        ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();
        context.setAttribute("exchangeRateService", exchangeRateService);

        ExchangeService exchangeService = new ExchangeServiceImpl();
        context.setAttribute("exchangeService", exchangeService);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}
}
