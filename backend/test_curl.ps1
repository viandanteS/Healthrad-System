$body = @{email='frontoffice@healthrad.it';password='password'} | ConvertTo-Json
$res = Invoke-RestMethod -Uri http://localhost:8080/api/auth/login -Method Post -Body $body -ContentType 'application/json'
$token = $res.access_token
Write-Output "=== LOGIN OK ==="

# Test ricerca clienti
try {
    $clienti = Invoke-RestMethod -Uri "http://localhost:8080/api/clienti?q=CLI" -Headers @{Authorization="Bearer $token"}
    Write-Output "=== RICERCA CLIENTI OK: $($clienti.Length) risultati ==="
    $clienti | ForEach-Object { Write-Output "  CF: $($_.cf) - $($_.nome) $($_.cognome)" }
} catch {
    Write-Output "=== RICERCA CLIENTI FALLITA ==="
    Write-Output $_.Exception.Message
}

# Test creazione prenotazione
try {
    $prenotazione = @{
        tipologia = 'RX Torace'
        ambulatorio = @{ codiceAmbulatorio = 'A01' }
        cliente = @{ cf = 'CLI0000000000001' }
        addetto = @{ cf = 'FO00000000000001' }
        dataPrenotazione = '2026-04-10'
        orarioPrenotazione = '09:00:00'
    } | ConvertTo-Json -Depth 3
    $saved = Invoke-RestMethod -Uri "http://localhost:8080/api/prenotazioni" -Method Post -Body $prenotazione -ContentType 'application/json' -Headers @{Authorization="Bearer $token"}
    Write-Output "=== PRENOTAZIONE CREATA OK: id=$($saved.id) stato=$($saved.stato) ==="
} catch {
    Write-Output "=== PRENOTAZIONE FALLITA ==="
    $stream = $_.Exception.Response.GetResponseStream()
    if ($stream) {
        $reader = New-Object System.IO.StreamReader($stream)
        Write-Output "Error Body: $($reader.ReadToEnd())"
    } else {
        Write-Output $_.Exception.Message
    }
}
