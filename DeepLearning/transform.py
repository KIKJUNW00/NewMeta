import pandas as pd
import os

# CSV 파일 로드
file_path = "D:\코딩\Python\AI\AI_Project\Deep_Learning\Data\실험데이터.csv"
df = pd.read_csv(file_path)

if os.path.exists(file_path):
    print("파일이 존재합니다.")
else:
    print("파일이 존재하지 않습니다. 경로를 확인하세요.")

# 변경할 패턴 정의
busan_pattern = [
    "Busan_yangsan", "Busan_yangsan",
    "Busan_dongnea", "Busan_dongnea",
    "Busan_gumjung", "Busan_gumjung"
]

hub_type_patterns = {
    "Busan": ["Busan_yangsan", "Busan_yangsan", "Busan_dongnea", "Busan_dongnea", "Busan_gumjung", "Busan_gumjung"],
    "Daegu": ["Daegu_donggu", "Daegu_donggu", "Daegu_junggu", "Daegu_junggu", "Daegu_bukgu", "Daegu_bukgu"],
    "Daejeon": ["Daejeon_donggu", "Daejeon_donggu", "Daejeon_junggu", "Daejeon_junggu", "Daejeon_bukgu", "Daejeon_bukgu"],
    "Gwangju": ["Gwangju_donggu", "Gwangju_donggu", "Gwangju_junggu", "Gwangju_junggu", "Gwangju_bukgu", "Gwangju_bukgu"],
    "Seoul": ["Seoul_donggu", "Seoul_donggu", "Seoul_junggu", "Seoul_junggu", "Seoul_bukgu", "Seoul_bukgu"]
}

cities_to_change = ["Busan", "Daegu", "Daejeon", "Gwangju", "Seoul"]

# hub_type 변경 로직 적용
for city in cities_to_change:
    city_rows = df[df["hub_type"] == city].copy()  # 해당 도시 필터링
    repeat_count = (len(city_rows) // len(hub_type_patterns[city])) + 1  # 패턴 반복 횟수 계산
    df.loc[df["hub_type"] == city, "hub_type"] = (hub_type_patterns[city] * repeat_count)[:len(city_rows)]  # 변경 적용

# 변환된 데이터 저장
output_file = "modified_dummy02.csv"
df.to_csv(output_file, index=False)

print(f"변환 완료! 저장된 파일: {output_file}")
