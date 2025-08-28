create database database_training;
use database_training;

-- 1. Bảng Teachers
CREATE TABLE Teachers (
    teacher_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    subject VARCHAR(50) NOT NULL
);

-- 2. Bảng Classes
CREATE TABLE Classes (
    class_id INT PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL,
    teacher_id INT,
    FOREIGN KEY (teacher_id) REFERENCES Teachers(teacher_id)
);

-- 3. Bảng Students
CREATE TABLE Students (
    student_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    class_id INT,
    FOREIGN KEY (class_id) REFERENCES Classes(class_id)
);

-- Insert dữ liệu mẫu cho Teachers
INSERT INTO Teachers (teacher_id, name, subject) VALUES
(1, 'Cô Lan', 'Toán'),
(2, 'Thầy Minh', 'Văn'),
(3, 'Cô Hoa', 'Anh');

-- Insert dữ liệu mẫu cho Classes
INSERT INTO Classes (class_id, class_name, teacher_id) VALUES
(1, 'Toán', 1),
(2, 'Văn', 2),
(3, 'Anh', 3);

-- Insert dữ liệu mẫu cho Students
INSERT INTO Students (student_id, name, age, class_id) VALUES
(1, 'An', 18, 1),
(2, 'Bình', 17, 1),
(3, 'Chi', 18, 2),
(4, 'Dũng', 19, 2),
(5, 'Hạnh', 17, 3);

-- Lấy danh sách tất cả học sinh.
select * from Students;
-- Lấy các học sinh có tuổi > 17.
select * from Students where age>17;
-- Đếm số lượng học sinh trong từng lớp (GROUP BY class_id).
Select c.class_name,Count(*) from Students AS s Join Classes AS c 
On s.class_id=c.class_id
group by c.class_id;
-- Lấy danh sách học sinh sắp xếp theo tuổi giảm dần.
Select * from Students
order by age desc;
-- Lấy danh sách học sinh kèm tên lớp của họ.
Select s.*,c.class_name from Students AS s Join Classes AS c
On s.class_id=c.class_id;
-- Đếm số học sinh mỗi lớp, chỉ hiện các lớp có từ 2 học sinh trở lên.
Select c.class_name,Count(c.class_name) from Students AS s Join Classes AS c
On s.class_id=c.class_id
group by c.class_name
having Count(*)> 1;
-- Lấy tên giáo viên dạy môn Văn và danh sách học sinh trong lớp đó.
Select t.name AS name_teacher,c.class_name, s.* from Students AS s Join Classes AS c 
On s.class_id = c.class_id
Join Teachers AS t
On c.teacher_id= t.teacher_id
where c.class_name="Văn";
-- Lấy tất cả học sinh thuộc lớp Toán.
Select s.*, c.class_name from Students AS s Join Classes AS c
On s.class_id = c.class_id
where c.class_name="Toán";
-- Tìm lớp có nhiều học sinh nhất.
Select c.class_name,Count(*) AS total_student from Students AS s Join Classes AS c
On s.class_id = c.class_id
group by c.class_name
order by total_student Desc 
limit 1;
-- Lấy danh sách học sinh kèm tên lớp và tên giáo viên chủ nhiệm (JOIN 3 bảng).
-- Lấy ra môn học có ít học sinh nhất.
-- Với mỗi giáo viên, cho biết số lớp họ phụ trách và tổng số học sinh của các lớp đó.



