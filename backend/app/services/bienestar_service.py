# app/Services/bienestar_service.py
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.models.registro_bienestar import RegistroBienestar
from app.models.paciente import Paciente
from app.schemas.bienestar import RegistroBienestarCreate
import json

class BienestarService:
    def __init__(self, db: AsyncSession):
        self.db = db

    async def get_paciente_id_by_cuenta_id(self, id_cuenta: int) -> int:
        """Obtiene el id_paciente asociado a un id_cuenta."""
        result = await self.db.execute(
            select(Paciente.id_paciente).where(Paciente.id_cuenta == id_cuenta)
        )
        paciente_id = result.scalar_one_or_none()
        if not paciente_id:
            raise ValueError(f"No se encontró un paciente para la cuenta ID: {id_cuenta}")
        return paciente_id

    async def create_registro_bienestar(self, id_paciente: int, registro_data: RegistroBienestarCreate) -> RegistroBienestar:
        sintomas_json = json.dumps(registro_data.sintomas_fisicos) if registro_data.sintomas_fisicos else None

        db_registro = RegistroBienestar(
            id_paciente=id_paciente,
            estado_emocional=registro_data.estado_emocional,
            sintomas_fisicos=sintomas_json,
            nota_adicional=registro_data.nota_adicional
        )
        self.db.add(db_registro)
        await self.db.commit()
        await self.db.refresh(db_registro)
        return db_registro