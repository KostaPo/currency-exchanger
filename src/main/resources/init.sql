PRAGMA foreign_keys=on;

CREATE TABLE IF NOT EXISTS Currencies (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Code VARCHAR NOT NULL,
    FullName VARCHAR NOT NULL,
    Sign VARCHAR
);

CREATE INDEX IF NOT EXISTS currencies_id_index ON Currencies (ID);
CREATE UNIQUE INDEX IF NOT EXISTS curr_code_unique_index ON Currencies (Code);

INSERT INTO Currencies (Code, FullName, Sign) VALUES ('RUB', 'Рубль', '₽');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('EUR', 'Евро', '€');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('USD', 'Доллар', '$');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('TRY', 'Лира', '₺');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('JPY', 'Иена', '¥');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('KZT', 'Тенге', '₸');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('GBP', 'Фунт', '£');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('ILS', 'Шекель', '₪');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('CNY', 'Юань', '¥');
INSERT INTO Currencies (Code, FullName, Sign) VALUES ('AZN', 'Манат', '₼');

CREATE TABLE IF NOT EXISTS ExchangeRates (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId INTEGER NOT NULL,
    TargetCurrencyId INTEGER NOT NULL,
    Rate Decimal(6) NOT NULL,
    CONSTRAINT fk_b_curr FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies (ID) ON DELETE CASCADE,
    CONSTRAINT fk_t_curr FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies (ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS exchange_rates_id_index ON ExchangeRates (ID);
CREATE UNIQUE INDEX IF NOT EXISTS curr_pair_unique_index ON ExchangeRates (BaseCurrencyId, TargetCurrencyId);

INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (3, 1, 90.80);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (1, 2, 0.0099);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (4, 6, 16.53);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (5, 7, 0.0055);
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (3, 7, 0.78);