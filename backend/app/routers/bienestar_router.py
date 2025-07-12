from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from app.database.session import get_db
from app.schemas.bienestar import RegistroBienestarCreate, RegistroBienestarResponse
from app.services.bienestar_service import BienestarService
from app.auth.jwt import get_current_user_id

router = APIRouter(
    prefix="/api",
    tags=["Bienestar"]
)

@router.post("/bienestar", response_model=RegistroBienestarResponse, status_code=status.HTTP_201_CREATED)
async def registrar_bienestar(
    registro_data: RegistroBienestarCreate,
    db: AsyncSession = Depends(get_db),
    id_cuenta_actual: int = Depends(get_current_user_id)
):
    service = BienestarService(db)
    try:
        id_paciente = await service.get_paciente_id_by_cuenta_id(id_cuenta_actual)

        nuevo_registro = await service.create_registro_bienestar(id_paciente, registro_data)
        return nuevo_registro
    except ValueError as ve:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=str(ve)
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error al guardar el registro de bienestar: {e}"
        )