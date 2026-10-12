/* ============================================================
   Database: CourseManagementDB
   Mo ta: Script tao database, 3 bang (CourseCategories, Courses,
   CourseImages), quan he va du lieu mau toi thieu theo yeu cau de bai.
   Chay toan bo script nay trong SQL Server Management Studio (SSMS)
   hoac Azure Data Studio voi quyen du de tao database.
   ============================================================ */

IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE name = 'CourseManagementDB')
BEGIN
    CREATE DATABASE CourseManagementDB;
END
GO

USE CourseManagementDB;
GO

/* ---------- Xoa bang cu (neu chay lai script) ---------- */
IF OBJECT_ID('dbo.CourseImages', 'U') IS NOT NULL DROP TABLE dbo.CourseImages;
IF OBJECT_ID('dbo.Courses', 'U') IS NOT NULL DROP TABLE dbo.Courses;
IF OBJECT_ID('dbo.CourseCategories', 'U') IS NOT NULL DROP TABLE dbo.CourseCategories;
GO

/* ---------- Bang CourseCategories ---------- */
CREATE TABLE dbo.CourseCategories (
    Id            INT IDENTITY(1,1) PRIMARY KEY,
    Name          NVARCHAR(255) NOT NULL,
    Description   NVARCHAR(MAX) NULL
);
GO

/* ---------- Bang Courses ---------- */
CREATE TABLE dbo.Courses (
    Id                 INT IDENTITY(1,1) PRIMARY KEY,
    Name               NVARCHAR(255) NOT NULL,
    TuitionFee         DECIMAL(18,2) NOT NULL,
    Duration           INT NOT NULL,              -- so gio
    IsActive           BIT NOT NULL DEFAULT 1,
    Description        NVARCHAR(MAX) NULL,
    CourseCategoryId   INT NOT NULL,
    CONSTRAINT FK_Courses_CourseCategories
        FOREIGN KEY (CourseCategoryId) REFERENCES dbo.CourseCategories(Id),
    CONSTRAINT CK_Courses_TuitionFee CHECK (TuitionFee > 0),
    CONSTRAINT CK_Courses_Duration CHECK (Duration > 0)
);
GO

/* Ten khoa hoc khong duoc trung trong cung mot loai khoa hoc */
CREATE UNIQUE INDEX UQ_Courses_Name_Category
    ON dbo.Courses(CourseCategoryId, Name);
GO

/* ---------- Bang CourseImages ---------- */
CREATE TABLE dbo.CourseImages (
    Id            INT IDENTITY(1,1) PRIMARY KEY,
    ImageUrl      NVARCHAR(500) NOT NULL,
    IsThumbnail   BIT NOT NULL DEFAULT 0,
    CourseId      INT NOT NULL,
    CONSTRAINT FK_CourseImages_Courses
        FOREIGN KEY (CourseId) REFERENCES dbo.Courses(Id) ON DELETE CASCADE
);
GO

/* Moi khoa hoc chi co toi da MOT anh dai dien (chan o tang DB) */
CREATE UNIQUE INDEX UQ_CourseImages_OneThumbnailPerCourse
    ON dbo.CourseImages(CourseId)
    WHERE IsThumbnail = 1;
GO

/* ============================================================
   DU LIEU MAU (toi thieu 3 loai khoa hoc, 5 khoa hoc, moi khoa
   hoc co anh)
   ============================================================ */

INSERT INTO dbo.CourseCategories (Name, Description) VALUES
(N'Lập trình Web', N'Các khóa học liên quan đến phát triển ứng dụng web'),
(N'Lập trình Mobile', N'Các khóa học phát triển ứng dụng di động'),
(N'Khoa học dữ liệu', N'Các khóa học về dữ liệu, AI, Machine Learning');
GO

INSERT INTO dbo.Courses (Name, TuitionFee, Duration, IsActive, Description, CourseCategoryId) VALUES
(N'ASP.NET Core', 3500000, 40, 1, N'Xây dựng web API và MVC với ASP.NET Core', 1),
(N'ReactJS', 2800000, 25, 1, N'Xây dựng giao diện web với ReactJS', 1),
(N'Java Spring Boot', 4000000, 50, 1, N'Phát triển backend với Spring Boot', 1),
(N'Flutter', 3000000, 35, 0, N'Xây dựng ứng dụng di động đa nền tảng với Flutter', 2),
(N'Machine Learning cơ bản', 4500000, 60, 1, N'Nhập môn học máy và dữ liệu', 3);
GO

INSERT INTO dbo.CourseImages (ImageUrl, IsThumbnail, CourseId) VALUES
('https://example.com/images/aspnet-1.jpg', 1, 1),
('https://example.com/images/aspnet-2.jpg', 0, 1),
('https://example.com/images/react-1.jpg', 1, 2),
('https://example.com/images/springboot-1.jpg', 1, 3),
('https://example.com/images/springboot-2.jpg', 0, 3),
('https://example.com/images/flutter-1.jpg', 1, 4),
('https://example.com/images/ml-1.jpg', 1, 5);
GO

/* ---------- Kiem tra nhanh ---------- */
SELECT * FROM dbo.CourseCategories;
SELECT * FROM dbo.Courses;
SELECT * FROM dbo.CourseImages;
GO
