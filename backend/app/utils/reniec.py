import os
import httpx
from typing import Optional, Dict

RENIEC_URL   = "https://api.apis.net.pe/v2/reniec/dni"
RENIEC_TOKEN = os.getenv("RENIEC_TOKEN")

async def validar_dni(dni: str, digito_verificador: str) -> Optional[Dict]:
    """
    Llama a la API de RENIEC y retorna el JSON si válido,
    o None si falla o el dígito no coincide.
    """
    headers = {"Authorization": f"Bearer {RENIEC_TOKEN}"}
    params  = {"numero": dni}

    async with httpx.AsyncClient() as client:
        resp = await client.get(RENIEC_URL, headers=headers, params=params)
        if resp.status_code != 200:
            return None
        data = resp.json()
        if data.get("digitoVerificador") != digito_verificador:
            return None
        return data
