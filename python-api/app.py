import os
import tempfile

from shazamio import Shazam
from fastapi import FastAPI, UploadFile, HTTPException

app = FastAPI()
shazam = Shazam()


@app.post("/detect/")
async def detect(file: UploadFile):
    if file.content_type != "audio/ogg":
        raise HTTPException(status_code=400, detail="Invalid file type. Please upload an .ogg audio file.")

    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix=".ogg") as temp_file:
            temp_file.write(await file.read())
            temp_file_path = temp_file.name
            result = await shazam.recognize(temp_file_path)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Music detection failed: {str(e)}")
    finally:
        try:
            os.remove(temp_file_path)
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Temporary file could not be deleted: {str(e)}")

    return {"result": result}
