import pymysql, os
conn = pymysql.connect(
    host     = "bkbbioe2fmpynubi9izf-mysql.services.clever-cloud.com",
    user     = "uesa0zudlw90rdrk",
    password = "VTpo8DY4L807EWdCbrb0",
    database = "bkbbioe2fmpynubi9izf",
    port     = 3306
)
print("¡Conectado OK!")  
conn.close()