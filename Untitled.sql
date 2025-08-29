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

-- 4. Bảng TeacherHierarchy(phân cấp giáo viên) 
CREATE TABLE TeacherHierarchy (
    teacher_id INT,
    manager_id INT
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
(3, 'Anh', 3),
(4,'Tin',null);

-- Insert dữ liệu mẫu cho Students
INSERT INTO Students (student_id, name, age, class_id) VALUES
(1, 'An', 18, 1),
(2, 'Bình', 17, 1),
(3, 'Chi', 18, 2),
(4, 'Dũng', 19, 2),
(5, 'Hạnh', 17, 3),
(6,'Nam', '20', null);

-- Insert dữ liệu mẫu cho TeacherHierarchy
INSERT INTO TeacherHierarchy (teacher_id, manager_id) VALUES
(1,null),
(2,1),
(3,2);

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
Select s.name, c.class_name, t.name AS name_teacher from Students AS s Join Classes AS c 
On s.class_id = c.class_id
Join Teachers AS t
On c.teacher_id= t.teacher_id; 
-- Lấy ra môn học có ít học sinh nhất.
Select c.class_name, Count(s.student_id) AS So_hoc_sinh  from Students AS s Join Classes AS c
On s.class_id = c.class_id
group by c.class_name 
order by Count(s.student_id) asc
limit 1;
-- Với mỗi giáo viên, cho biết số lớp họ phụ trách và tổng số học sinh của các lớp đó.
Select t.name AS name_teacher, Count(c.class_name) AS so_lop_day, Count(s.student_id) AS Tong_so_hoc_sinh
from Students AS s Join Classes AS c 
On s.class_id = c.class_id
Join Teachers AS t
On c.teacher_id=t.teacher_id
group by t.name;

-- inner join 
SELECT s.name, c.class_name
FROM Students s
INNER JOIN Classes c ON s.class_id = c.class_id;
-- left join
SELECT s.name, c.class_name
FROM Students s
LEFT JOIN Classes c ON s.class_id = c.class_id;
-- right join
SELECT s.name, c.class_name
FROM Students s
RIGHT JOIN Classes c ON s.class_id = c.class_id;
-- full join -  mysql kh hỗ trợ full join dùng union để kết hợp 2 lại = full join
SELECT s.name, c.class_name
FROM Students s
LEFT JOIN Classes c ON s.class_id = c.class_id
UNION
SELECT s.name, c.class_name
FROM Students s
RIGHT JOIN Classes c ON s.class_id = c.class_id;
-- CROSS JOIN
SELECT s.name, c.class_name
FROM Students s
CROSS JOIN Classes c;
-- SELF JOIN
SELECT a.name AS student1, b.name AS student2, c.class_name
FROM Students a
JOIN Students b ON a.class_id = b.class_id AND a.student_id < b.student_id
JOIN Classes c ON a.class_id = c.class_id;

Select name, age from Students;
-- Subquery: Tìm học sinh lớn tuổi nhất trong trường.
Select name, age from Students 
where age=(Select Max(age) from Students );
-- Subquery: Tìm danh sách học sinh học các lớp do Cô Lan phụ trách.
select s.name, c.class_name from Students AS s Join Classes AS c 
On s.class_id = c.class_id
where c.teacher_id IN (
	Select teacher_id from Teachers
    where name like '%Lan%'
);
-- Subquery: Tính số lượng học sinh của từng lớp, rồi chọn ra lớp có nhiều học sinh nhất.
Select c.class_name, s.total_students
from (SELECT class_id, COUNT(*) AS total_students
FROM Students
WHERE class_id IS NOT NULL
GROUP BY class_id) s
Join Classes c ON s.class_id = c.class_id
order by s.total_students DESC
limit 1;

-- Subquery: Tìm tất cả học sinh có tuổi bằng tuổi lớn nhất trong lớp của mình.
select s.name, s.age, c.class_name from Students s
join Classes c ON s.class_id = c.class_id
where s.age=(
	select max(age) from Students where class_id=s.class_id
);    
    
-- Subquery: Liệt kê các giáo viên đang phụ trách ít nhất một lớp có học sinh.
select t.name, t.subject from Teachers t
where exists (
	select 1 
    from Students s join Classes c
    on c.teacher_id=t.teacher_id
);

-- CTE: Viết query hiển thị: class_name, teacher_name, total_students của mỗi lớp.
with Total_student AS (
	Select class_id,Count(*) AS total_student from Students s 
    group by class_id
)
select c.class_name,t.name, ts.total_student
from Classes c inner join Total_student ts on c.class_id= ts.class_id
join Teachers t on t.teacher_id=c.teacher_id;

-- CTE: Dùng CTE để tìm ra lớp có số học sinh nhiều nhất, kèm tên giáo viên phụ trách.
WITH Count_student AS (
    SELECT class_id, COUNT(*) AS count_student
    FROM Students
    GROUP BY class_id
),
MaxCount AS (
    SELECT MAX(count_student) AS max_student
    FROM Count_student
)
SELECT c.class_name, t.name AS teacher_name, cs.count_student
FROM Count_student cs
JOIN MaxCount m ON cs.count_student = m.max_student
JOIN Classes c ON cs.class_id = c.class_id
JOIN Teachers t ON t.teacher_id = c.teacher_id;
-- CTE: Liệt kê teacher_name nào đang dạy lớp mà có ít nhất 1 học sinh.
with Exists_student AS (
	select s.class_id, count(s.student_id) as count_student from Students s
    join  Classes c on s.class_id=c.class_id
    group by c.class_id
    having count_student>0
)
select t.name, es.count_student from  Exists_student es 
join Classes c on es.class_id= c.class_id
join Teachers t on t.teacher_id= c.teacher_id;

-- ROW_NUMBER()
Select name, age, row_number() over (order by age DESC) from Students;
-- RANK()
Select name, age, rank() over (order by age DESC) from Students;
-- DENSE_RANK()
Select name, age, dense_rank() over (order by age DESC) from Students;
-- VIEW
Create view StudentAge17 AS
select student_id,name, age from Students
Where age> 17;
select * from StudentAge17;

UPDATE StudentAge17
SET age = 21
WHERE student_id = 1;

-- View phức tạp chỉ có thể select, kh insert, update, delete được 
CREATE VIEW StudentCountPerClass AS
SELECT c.class_name, COUNT(s.student_id) AS total_students
FROM Students s
JOIN Classes c ON s.class_id = c.class_id
GROUP BY c.class_name;


Select * from StudentCountPerClass;

UPDATE StudentCountPerClass
SET total_students = 10
WHERE class_name = 'Toán';
-- Stored Procedure
DELIMITER //
Create procedure GetStudentByClass(IN p_class_id INT)
BEGIN 
	Select s.student_id,s.name,s.age,c.class_name
    from Students s 
    JOIN Classes c ON s.class_id = c.class_id
    WHERE s.class_id = p_class_id;
END//
DELIMITER ;

call GetStudentByClass(1);








