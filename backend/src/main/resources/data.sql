-- Dati seed gestiti programmaticamente da DataInitializer.java
-- Gli utenti vengono creati a runtime con BCrypt via jBCrypt per evitare
-- incompatibilità con hash generati da altri linguaggi.

-- Mock ambulatori
INSERT IGNORE INTO ambulatorio (codice_ambulatorio) VALUES ('AMB_01');
INSERT IGNORE INTO ambulatorio (codice_ambulatorio) VALUES ('AMB_02');
INSERT IGNORE INTO ambulatorio (codice_ambulatorio) VALUES ('AMB_03');
