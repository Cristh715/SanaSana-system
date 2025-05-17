from sqlalchemy import Column, Integer, String, Boolean, DateTime
from datetime import datetime
from app.database.session import Base

class CuentaPaciente(Base):
    __tablename__ = "cuenta_paciente"

    id_cuenta       = Column("id_cuenta", Integer, primary_key=True, index=True)
    correo          = Column(String(255), unique=True, nullable=False, index=True)
    contrasena_hash = Column(String(255), nullable=False)
    cuenta_bloqueada= Column(Boolean, default=False)
    fecha_registro  = Column(DateTime, default=datetime.utcnow)
