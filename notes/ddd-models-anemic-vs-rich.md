# So sánh Mô hình Domain: Anemic vs. Rich DDD

Tài liệu này hệ thống hóa kiến thức về hai phương pháp thiết kế Domain Model trong phát triển phần mềm theo hướng DDD (Domain-Driven Design).

---

## 1. Tổng quan hai cách tiếp cận

### Approach A: Anemic Domain Model (Mô hình "Thiếu máu")
Đây là mô hình dữ liệu tập trung (Data-Driven), trong đó các Class Domain chủ yếu đóng vai trò là vật chứa dữ liệu.

*   **Đặc điểm:** Class chứa nhiều Fields và Public Setters. Logic nghiệp vụ (validation, rules) được đẩy hoàn toàn ra tầng **Application Service**.
*   **Mục tiêu chính:** Tối ưu cho việc **Tracking & Auditing**. Hệ thống thường tự động bắn các event dạng `FIELD_VALUE_CHANGED` mỗi khi setter được gọi.

### Approach B: Rich Domain Model (Mô hình "Giàu có")
Đây là mô hình hành vi tập trung (Behavior-Driven), chuẩn mực theo lý thuyết DDD.

*   **Đặc điểm:** Logic nghiệp vụ nằm ngay trong Domain Class. Các Setters thường là Private hoặc Protected để đảm bảo tính bao đóng.
*   **Mục tiêu chính:** Bảo vệ tính toàn vẹn của dữ liệu (Invariants) và thể hiện rõ ràng ngữ nghĩa nghiệp vụ thông qua các hành vi (Methods).

---

## 2. So sánh chi tiết

| Tiêu chí | Anemic Model (Approach A) | Rich Model (Approach B) |
| :--- | :--- | :--- |
| **Vị trí logic** | Nằm ở Service Layer. Domain chỉ chứa data. | Nằm trong Domain Class. Service chỉ điều phối. |
| **Tính bao đóng** | Thấp. Setter cho phép sửa dữ liệu tùy ý. | Cao. Dữ liệu chỉ đổi qua các hàm nghiệp vụ. |
| **Events** | **Generic:** Ví dụ `FIELD_VALUE_CHANGED`. | **Semantic:** Ví dụ `UserLocked`, `PasswordChanged`. |
| **Tính toàn vẹn** | Rủi ro cao nếu gọi setter rời rạc. | Đảm bảo tuyệt đối thông qua các hàm đóng gói. |
| **Mục tiêu** | Tối ưu CRUD, Auditing, Audit Log tự động. | Tối ưu nghiệp vụ phức tạp, bảo vệ Invariants. |

---

## 3. Đánh giá Ưu và Nhược điểm

### ✅ Anemic Domain Model (Approach A)
*   **Ưu điểm:**
    *   **Audit Log hoàn hảo:** Tự động ghi lại "Ai đã sửa trường gì, từ giá trị nào sang giá trị nào" mà không cần code thủ công.
    *   **Dễ tiếp cận:** Phù hợp với Junior Dev, các dự án mang tính chất CRUD nhiều.
    *   **Frontend Friendly:** Dễ dàng hiển thị lịch sử thay đổi trên giao diện Admin Dashboard.
*   **Nhược điểm:**
    *   **Mất đi ý nghĩa (Lost Intent):** Không biết *tại sao* dữ liệu thay đổi (ví dụ: đổi pass do reset hay do user tự đổi).
    *   **Service phình to:** Logic dồn hết về Service dẫn đến class dài và nhiều `if-else`.

### ❌ Rich Domain Model (Approach B)
*   **Ưu điểm:**
    *   **Ngữ nghĩa rõ ràng:** Mỗi hành vi (method) đều mang ý nghĩa nghiệp vụ cụ thể.
    *   **An toàn:** Không cho phép object rơi vào trạng thái không hợp lệ (ví dụ: lock user nhưng quên set ngày khóa).
*   **Nhược điểm:**
    *   **Khó cài đặt Audit Log:** Phải tự định nghĩa log cho từng hành vi nghiệp vụ.
    *   **Phức tạp hơn:** Đòi hỏi dev có tư duy thiết kế tốt và hiểu sâu nghiệp vụ.

---

## 4. Giải pháp kết hợp (Hybrid Approach)

Nếu dự án yêu cầu tính Audit chặt chẽ (Approach A) nhưng bạn muốn giữ được ngữ nghĩa nghiệp vụ (Approach B), có thể áp dụng mô hình **Business Logic Wrapper**:

```java
// Vẫn giữ các setter bắn event generic để Audit tự động
public void setStatus(UserStatus status) {
    // Logic bắn event FIELD_VALUE_CHANGED tự động
    this.status = status;
}

/**
 * Hàm nghiệp vụ (Wrapper) bao bọc logic.
 * Vừa đảm bảo Audit, vừa thể hiện rõ Intent.
 */
public void lock(String reason) {
    // 1. Kiểm tra điều kiện nghiệp vụ
    if (this.status == UserStatus.LOCKED) return;

    // 2. Gọi setter để hệ thống tự động lưu Audit Log
    this.setStatus(UserStatus.LOCKED);

    // 3. (Optional) Bắn thêm event nghiệp vụ cụ thể
    addEvent(new UserLockedEvent(this.id, reason));
}
```

---

## 5. Kết luận

*   **Anemic Model:** Phổ biến trong các hệ thống Enterprise cần audit log cực kỳ chi tiết và tự động.
*   **Rich Model:** Chuẩn mực cho các hệ thống có logic nghiệp vụ cực kỳ phức tạp và cần bảo vệ dữ liệu nghiêm ngặt.

**Lời khuyên:** Trong môi trường làm việc nhóm, sự nhất quán (Consistency) quan trọng hơn tính đúng đắn về lý thuyết. Nếu dự án đã chọn một phong cách, hãy tuân thủ nó và cải tiến khéo léo để khắc phục các nhược điểm của nó.
