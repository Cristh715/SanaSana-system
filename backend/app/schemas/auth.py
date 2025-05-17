from pydantic import BaseModel

class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
    
class LoginRequest(BaseModel):
    email: str
    password: str
class LoginResponse(BaseModel):
    dni: str
    email: str
    token: Token
