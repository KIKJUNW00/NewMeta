from preprocessing_copy import preprocess_pipeline
from tensorflow.keras.models import load_model
import numpy as np
import pandas as pd
import requests
from fastapi import FastAPI, Request
import uvicorn

#python -m uvicorn AutoEncoder:app --reload

app = FastAPI()

# 저장된 모델 로드
autoencoder = load_model('autoencoder_model2.h5')
seq_length = 3

@app.post("/detect_anomaly")
async def analyze_data(request: Request):
    data = await request.json()
    df_anomaly = pd.DataFrame(data["records"])  # Spring Boot에서 받은 데이터를 DataFrame으로 변환

    # 데이터 전처리
    X_anomaly = preprocess_pipeline(df_anomaly, seq_length)

    # 정상 데이터 로드 및 평가
    X_normal = preprocess_pipeline("Dummy_01.csv", seq_length)
    reconstructed_normal = autoencoder.predict(X_normal)
    normal_reconstruction_errors = np.mean(np.abs(X_normal - reconstructed_normal), axis=(1, 2))

    # 모델 평가
    reconstructed = autoencoder.predict(X_anomaly)
    reconstruction_errors = np.mean(np.abs(X_anomaly - reconstructed), axis=(1, 2))  # MSE → MAE 적용
    print("Reconstruction errors calculated.")

    # 정상 데이터 기반 threshold 설정 (3σ 적용)
    threshold = np.mean(normal_reconstruction_errors) + 3 * np.std(normal_reconstruction_errors)

    # epc_code별 이상치 개수 분석
    anomalies = reconstruction_errors > threshold
    df_anomaly = df_anomaly.iloc[-len(X_anomaly):]  # 테스트 데이터에 해당하는 부분만 선택
    df_anomaly["reconstruction_error"] = reconstruction_errors
    df_anomaly["anomaly"] = anomalies

    # 이상치 탐지
    print(f"Number of anomalies detected: {np.sum(anomalies)}")

    # epc_code별 이상치 개수 집계
    epc_anomaly_counts = df_anomaly.groupby("epc_code")["anomaly"].sum()

    # 이상치가 1개 이상인 epc_code만 출력
    epc_anomaly_status = epc_anomaly_counts > 1
    anomaly_results = []
    for epc_code, is_anomaly in epc_anomaly_status.items():
        result = {"epc_code": epc_code, "is_anomaly": bool(is_anomaly)}
        anomaly_results.append(result)

    return {"records": anomaly_results}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
    
@app.get("/")
async def read_root():
    return {"message": "AI프로젝트"}