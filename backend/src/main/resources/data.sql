-- Ambulatori
MERGE INTO ambulatorio KEY (codice_ambulatorio) VALUES ('A01');
MERGE INTO ambulatorio KEY (codice_ambulatorio) VALUES ('A02');
MERGE INTO ambulatorio KEY (codice_ambulatorio) VALUES ('B01');
MERGE INTO ambulatorio KEY (codice_ambulatorio) VALUES ('B02');
MERGE INTO ambulatorio KEY (codice_ambulatorio) VALUES ('C01');

-- I turni e le associazioni RAT/RDT sono inseriti dal DataInitializer
-- dopo la creazione degli utenti/dipendenti
