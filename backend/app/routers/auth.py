from fastapi import APIRouter, HTTPException, Depends
from app.database.session import SessionLocal 
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.auth import LoginRequest, LoginResponse

from app.services.auth_service import authenticate_user

router = APIRouter()

async def get_db():
    async with SessionLocal() as session:
        yield session

@router.post("/login", response_model=LoginResponse)
async def login(login_request: LoginRequest, db: AsyncSession = Depends(get_db)):

    email = login_request.email
    password = login_request.password
    
    if not email:
        raise HTTPException(status_code=400, detail="Email is required")
    
    if not password:
        raise HTTPException(status_code=400, detail="Password is required")
    
    return await authenticate_user(login_request, db)