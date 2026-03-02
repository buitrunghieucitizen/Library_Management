USE [master]
GO
/**\*\*** Object: Database [LibraryManager_V2] Script Date: 26/02/2026 8:20:39 CH **\*\***/
CREATE DATABASE [LibraryManager_V2]
CONTAINMENT = NONE
ON PRIMARY
( NAME = N'LibraryManager_V2', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\LibraryManager_V2.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
LOG ON
( NAME = N'LibraryManager_V2_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\LibraryManager_V2_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [LibraryManager_V2] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [LibraryManager_V2].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [LibraryManager_V2] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [LibraryManager_V2] SET ANSI_NULLS OFF
GO
ALTER DATABASE [LibraryManager_V2] SET ANSI_PADDING OFF
GO
ALTER DATABASE [LibraryManager_V2] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [LibraryManager_V2] SET ARITHABORT OFF
GO
ALTER DATABASE [LibraryManager_V2] SET AUTO_CLOSE ON
GO
ALTER DATABASE [LibraryManager_V2] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [LibraryManager_V2] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [LibraryManager_V2] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [LibraryManager_V2] SET CURSOR_DEFAULT GLOBAL
GO
ALTER DATABASE [LibraryManager_V2] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [LibraryManager_V2] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [LibraryManager_V2] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [LibraryManager_V2] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [LibraryManager_V2] SET ENABLE_BROKER
GO
ALTER DATABASE [LibraryManager_V2] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [LibraryManager_V2] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [LibraryManager_V2] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [LibraryManager_V2] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [LibraryManager_V2] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [LibraryManager_V2] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [LibraryManager_V2] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [LibraryManager_V2] SET RECOVERY SIMPLE
GO
ALTER DATABASE [LibraryManager_V2] SET MULTI_USER
GO
ALTER DATABASE [LibraryManager_V2] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [LibraryManager_V2] SET DB_CHAINING OFF
GO
ALTER DATABASE [LibraryManager_V2] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF )
GO
ALTER DATABASE [LibraryManager_V2] SET TARGET_RECOVERY_TIME = 60 SECONDS
GO
ALTER DATABASE [LibraryManager_V2] SET DELAYED_DURABILITY = DISABLED
GO
ALTER DATABASE [LibraryManager_V2] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [LibraryManager_V2] SET QUERY_STORE = ON
GO
ALTER DATABASE [LibraryManager_V2] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
ALTER AUTHORIZATION ON DATABASE::[LibraryManager_V2] TO [sa]
GO
USE [LibraryManager_V2]
GO
/**\*\*** Object: Table [dbo].[Author] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Author](
[AuthorID] [int] IDENTITY(1,1) NOT NULL,
[AuthorName] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED
(
[AuthorID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Author] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Book] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Book](
[BookID] [int] IDENTITY(1,1) NOT NULL,
[BookName] [nvarchar](200) NOT NULL,
[Quantity] [int] NOT NULL,
[Available] [int] NOT NULL,
[CategoryID] [int] NOT NULL,
[PublisherID] [int] NOT NULL,
PRIMARY KEY CLUSTERED
(
[BookID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Book] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[BookAuthor] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BookAuthor](
[BookID] [int] NOT NULL,
[AuthorID] [int] NOT NULL,
CONSTRAINT [PK_BookAuthor] PRIMARY KEY CLUSTERED
(
[BookID] ASC,
[AuthorID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[BookAuthor] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[BookCode] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BookCode](
[BookCodeID] [int] IDENTITY(1,1) NOT NULL,
[BookID] [int] NOT NULL,
[BookCode] [nvarchar](50) NOT NULL,
PRIMARY KEY CLUSTERED
(
[BookCodeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[BookCode] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[BookFile] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BookFile](
[BookFileID] [int] IDENTITY(1,1) NOT NULL,
[BookID] [int] NOT NULL,
[StaffID] [int] NOT NULL,
[FileName] [nvarchar](255) NOT NULL,
[FileUrl] [nvarchar](500) NOT NULL,
[FileType] [nvarchar](50) NULL,
[FileSize] [bigint] NULL,
[UploadAt] [datetime2](7) NOT NULL,
[IsActive] [bit] NOT NULL,
PRIMARY KEY CLUSTERED
(
[BookFileID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[BookFile] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[BookPrice] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BookPrice](
[BookID] [int] NOT NULL,
[PriceID] [int] NOT NULL,
[StartDate] [date] NOT NULL,
[EndDate] [date] NULL,
CONSTRAINT [PK_BookPrice] PRIMARY KEY CLUSTERED
(
[BookID] ASC,
[PriceID] ASC,
[StartDate] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[BookPrice] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Borrow] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Borrow](
[BorrowID] [int] IDENTITY(1,1) NOT NULL,
[StudentID] [int] NOT NULL,
[StaffID] [int] NOT NULL,
[BorrowDate] [date] NOT NULL,
[DueDate] [date] NOT NULL,
[Status] [nvarchar](20) NOT NULL,
[ReturnDate] [date] NULL,
PRIMARY KEY CLUSTERED
(
[BorrowID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Borrow] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[BorrowItem] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BorrowItem](
[BorrowID] [int] NOT NULL,
[BookID] [int] NOT NULL,
[Quantity] [int] NOT NULL,
CONSTRAINT [PK_BorrowItem] PRIMARY KEY CLUSTERED
(
[BorrowID] ASC,
[BookID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[BorrowItem] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Category] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Category](
[CategoryID] [int] IDENTITY(1,1) NOT NULL,
[CategoryName] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED
(
[CategoryID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Category] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[OrderDetail] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OrderDetail](
[OrderID] [int] NOT NULL,
[BookID] [int] NOT NULL,
[Quantity] [int] NOT NULL,
[UnitPrice] [decimal](12, 2) NOT NULL,
CONSTRAINT [PK_OrderDetail] PRIMARY KEY CLUSTERED
(
[OrderID] ASC,
[BookID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[OrderDetail] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Orders] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Orders](
[OrderID] [int] IDENTITY(1,1) NOT NULL,
[StudentID] [int] NOT NULL,
[StaffID] [int] NOT NULL,
[OrderDate] [date] NOT NULL,
[TotalAmount] [decimal](12, 2) NOT NULL,
[Status] [nvarchar](20) NULL,
PRIMARY KEY CLUSTERED
(
[OrderID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Orders] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Price] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Price](
[PriceID] [int] IDENTITY(1,1) NOT NULL,
[Amount] [decimal](12, 2) NOT NULL,
[Currency] [nvarchar](10) NULL,
[Note] [nvarchar](200) NULL,
PRIMARY KEY CLUSTERED
(
[PriceID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Price] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Publisher] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Publisher](
[PublisherID] [int] IDENTITY(1,1) NOT NULL,
[PublisherName] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED
(
[PublisherID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Publisher] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Role] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Role](
[RoleID] [int] IDENTITY(1,1) NOT NULL,
[RoleName] [nvarchar](50) NOT NULL,
PRIMARY KEY CLUSTERED
(
[RoleID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Role] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Staff] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Staff](
[StaffID] [int] IDENTITY(1,1) NOT NULL,
[StaffName] [nvarchar](100) NOT NULL,
[Username] [varchar](50) NULL,
[Password] [varchar](100) NULL,
PRIMARY KEY CLUSTERED
(
[StaffID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Staff] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[StaffRole] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StaffRole](
[StaffID] [int] NOT NULL,
[RoleID] [int] NOT NULL,
CONSTRAINT [PK_StaffRole] PRIMARY KEY CLUSTERED
(
[StaffID] ASC,
[RoleID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[StaffRole] TO SCHEMA OWNER
GO
/**\*\*** Object: Table [dbo].[Student] Script Date: 26/02/2026 8:20:40 CH **\*\***/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Student](
[StudentID] [int] IDENTITY(1,1) NOT NULL,
[StudentName] [nvarchar](100) NOT NULL,
[Email] [nvarchar](100) NULL,
[Phone] [nvarchar](20) NULL,
PRIMARY KEY CLUSTERED
(
[StudentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER AUTHORIZATION ON [dbo].[Student] TO SCHEMA OWNER
GO
SET IDENTITY_INSERT [dbo].[Author] ON

INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (1, N'Robert C. Martin')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (2, N'Nguyễn Nhật Ánh')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (3, N'Adam Smith')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (4, N'Nguyễn Nhật Ánh')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (5, N'Tô Hoài')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (6, N'J.K. Rowling')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (7, N'Joshua Bloch')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (8, N'Martin Fowler')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (9, N'Yuval Noah Harari')
INSERT [dbo].[Author] ([AuthorID], [AuthorName]) VALUES (10, N'Nam Cao')
SET IDENTITY_INSERT [dbo].[Author] OFF
GO
SET IDENTITY_INSERT [dbo].[Book] ON

INSERT [dbo].[Book] ([BookID], [BookName], [Quantity], [Available], [CategoryID], [PublisherID]) VALUES (4, N'Mắt Biếc', 15, 15, 3, 3)
INSERT [dbo].[Book] ([BookID], [BookName], [Quantity], [Available], [CategoryID], [PublisherID]) VALUES (5, N'The Wealth of Nations', 8, 8, 2, 2)
INSERT [dbo].[Book] ([BookID], [BookName], [Quantity], [Available], [CategoryID], [PublisherID]) VALUES (6, N'Cho tôi xin một vé đi tuổi thơ', 15, 12, 1, 2)
INSERT [dbo].[Book] ([BookID], [BookName], [Quantity], [Available], [CategoryID], [PublisherID]) VALUES (7, N'Dế Mèn phiêu lưu ký', 8, 6, 6, 3)
INSERT [dbo].[Book] ([BookID], [BookName], [Quantity], [Available], [CategoryID], [PublisherID]) VALUES (8, N'Harry Potter và Hòn đá Phù thủy', 10, 7, 4, 4)
INSERT [dbo].[Book] ([BookID], [BookName], [Quantity], [Available], [CategoryID], [PublisherID]) VALUES (9, N'Effective Java', 5, 4, 3, 5)
INSERT [dbo].[Book] ([BookID], [BookName], [Quantity], [Available], [CategoryID], [PublisherID]) VALUES (10, N'Refactoring', 4, 3, 3, 5)
INSERT [dbo].[Book] ([BookID], [BookName], [Quantity], [Available], [CategoryID], [PublisherID]) VALUES (11, N'Sapiens - Lược sử loài người', 12, 9, 2, 4)
SET IDENTITY_INSERT [dbo].[Book] OFF
GO
SET IDENTITY_INSERT [dbo].[Borrow] ON

INSERT [dbo].[Borrow] ([BorrowID], [StudentID], [StaffID], [BorrowDate], [DueDate], [Status], [ReturnDate]) VALUES (1, 1, 2, CAST(N'2025-01-01' AS Date), CAST(N'2025-01-10' AS Date), N'Returned', CAST(N'2026-02-26' AS Date))
INSERT [dbo].[Borrow] ([BorrowID], [StudentID], [StaffID], [BorrowDate], [DueDate], [Status], [ReturnDate]) VALUES (2, 2, 2, CAST(N'2025-01-05' AS Date), CAST(N'2025-01-15' AS Date), N'Returned', NULL)
INSERT [dbo].[Borrow] ([BorrowID], [StudentID], [StaffID], [BorrowDate], [DueDate], [Status], [ReturnDate]) VALUES (3, 6, 2, CAST(N'2026-02-26' AS Date), CAST(N'2026-02-28' AS Date), N'Returned', CAST(N'2026-02-26' AS Date))
INSERT [dbo].[Borrow] ([BorrowID], [StudentID], [StaffID], [BorrowDate], [DueDate], [Status], [ReturnDate]) VALUES (4, 3, 2, CAST(N'2026-02-26' AS Date), CAST(N'2026-02-28' AS Date), N'Returned', CAST(N'2026-02-26' AS Date))
INSERT [dbo].[Borrow] ([BorrowID], [StudentID], [StaffID], [BorrowDate], [DueDate], [Status], [ReturnDate]) VALUES (5, 1, 2, CAST(N'2026-02-10' AS Date), CAST(N'2026-02-24' AS Date), N'Borrowing', NULL)
INSERT [dbo].[Borrow] ([BorrowID], [StudentID], [StaffID], [BorrowDate], [DueDate], [Status], [ReturnDate]) VALUES (6, 2, 3, CAST(N'2026-01-15' AS Date), CAST(N'2026-02-15' AS Date), N'Returned', CAST(N'2026-02-12' AS Date))
INSERT [dbo].[Borrow] ([BorrowID], [StudentID], [StaffID], [BorrowDate], [DueDate], [Status], [ReturnDate]) VALUES (7, 3, 2, CAST(N'2026-02-20' AS Date), CAST(N'2026-03-06' AS Date), N'Borrowing', NULL)
INSERT [dbo].[Borrow] ([BorrowID], [StudentID], [StaffID], [BorrowDate], [DueDate], [Status], [ReturnDate]) VALUES (8, 1, 3, CAST(N'2026-02-01' AS Date), CAST(N'2026-02-15' AS Date), N'Overdue', NULL)
SET IDENTITY_INSERT [dbo].[Borrow] OFF
GO
SET IDENTITY_INSERT [dbo].[Category] ON

INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (18, N'CÃ´ng nghá»‡')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (8, N'Công nghệ')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (11, N'Economics')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (2, N'Fiction')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (3, N'History')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (14, N'Khoa há»c')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (6, N'Khoa học')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (17, N'Kinh táº¿')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (9, N'Kinh tế')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (15, N'Lá»‹ch sá»­')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (7, N'Lịch sử')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (12, N'Literature')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (16, N'OKOK')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (10, N'Programming')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (1, N'Science')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (4, N'Technology')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (13, N'VÄƒn há»c')
INSERT [dbo].[Category] ([CategoryID], [CategoryName]) VALUES (5, N'Văn học')
SET IDENTITY_INSERT [dbo].[Category] OFF
GO
SET IDENTITY_INSERT [dbo].[Price] ON

INSERT [dbo].[Price] ([PriceID], [Amount], [Currency], [Note]) VALUES (1, CAST(120000.00 AS Decimal(12, 2)), N'VND', N'Giá bìa')
INSERT [dbo].[Price] ([PriceID], [Amount], [Currency], [Note]) VALUES (2, CAST(85000.00 AS Decimal(12, 2)), N'VND', N'Giá khuyến mãi')
INSERT [dbo].[Price] ([PriceID], [Amount], [Currency], [Note]) VALUES (3, CAST(200000.00 AS Decimal(12, 2)), N'VND', N'Giá nhập khẩu')
INSERT [dbo].[Price] ([PriceID], [Amount], [Currency], [Note]) VALUES (4, CAST(95000.00 AS Decimal(12, 2)), N'VND', N'Giá bìa')
INSERT [dbo].[Price] ([PriceID], [Amount], [Currency], [Note]) VALUES (5, CAST(120000.00 AS Decimal(12, 2)), N'VND', N'Giá bìa - bản đặc biệt')
INSERT [dbo].[Price] ([PriceID], [Amount], [Currency], [Note]) VALUES (6, CAST(450000.00 AS Decimal(12, 2)), N'VND', N'Sách nhập khẩu')
INSERT [dbo].[Price] ([PriceID], [Amount], [Currency], [Note]) VALUES (7, CAST(320000.00 AS Decimal(12, 2)), N'VND', NULL)
SET IDENTITY_INSERT [dbo].[Price] OFF
GO
SET IDENTITY_INSERT [dbo].[Publisher] ON

INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (17, N'Nhà xuất bản Giáo dục')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (19, N'Nhà xuất bản Kim Đồng')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (20, N'Nhà xuất bản Thế Giới')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (18, N'Nhà xuất bản Trẻ')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (23, N'NXB Đại học Quốc gia')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (14, N'NXB GiÃ¡o dá»¥c')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (3, N'NXB Giáo dục')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (12, N'NXB Kim Äá»“ng')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (1, N'NXB Kim Đồng')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (15, N'NXB Lao Ä‘á»™ng')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (4, N'NXB Lao động')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (13, N'NXB Tráº»')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (2, N'NXB Trẻ')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (16, N'NXB VÄƒn há»c')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (5, N'NXB Văn học')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (21, N'O''Reilly')
INSERT [dbo].[Publisher] ([PublisherID], [PublisherName]) VALUES (22, N'Pearson')
SET IDENTITY_INSERT [dbo].[Publisher] OFF
GO
SET IDENTITY_INSERT [dbo].[Role] ON

INSERT [dbo].[Role] ([RoleID], [RoleName]) VALUES (1, N'Admin')
INSERT [dbo].[Role] ([RoleID], [RoleName]) VALUES (3, N'Assistant')
INSERT [dbo].[Role] ([RoleID], [RoleName]) VALUES (2, N'Librarian')
INSERT [dbo].[Role] ([RoleID], [RoleName]) VALUES (7, N'Sales')
INSERT [dbo].[Role] ([RoleID], [RoleName]) VALUES (4, N'Staff')
SET IDENTITY_INSERT [dbo].[Role] OFF
GO
SET IDENTITY_INSERT [dbo].[Staff] ON

INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (1, N'Pham Van D', N'staff_1', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (2, N'Hoang Thi E', N'staff_2', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (3, N'Nguyễn Văn A', N'staff_3', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (4, N'Trần Thị B', N'staff_4', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (5, N'Lê Văn C', N'staff_5', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (6, N'Nguyễn Văn An', N'staff_6', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (7, N'Trần Thị Bình', N'staff_7', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (8, N'Lê Hoàng Nam', N'staff_8', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (9, N'Phạm Minh Châu', N'staff_9', N'1')
INSERT [dbo].[Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (10, N'Admin Library', N'admin', N'123')
SET IDENTITY_INSERT [dbo].[Staff] OFF
GO
INSERT [dbo].[StaffRole] ([StaffID], [RoleID]) VALUES (1, 1)
INSERT [dbo].[StaffRole] ([StaffID], [RoleID]) VALUES (2, 2)
INSERT [dbo].[StaffRole] ([StaffID], [RoleID]) VALUES (3, 3)
INSERT [dbo].[StaffRole] ([StaffID], [RoleID]) VALUES (10, 1)
GO
SET IDENTITY_INSERT [dbo].[Student] ON

INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (1, N'Nguyen Van A', N'nva@example.com', N'0123456789')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (2, N'Tran Thi B', N'ttb@example.com', N'0987654321')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (3, N'Le Van C', N'lvc@example.com', N'0912345678')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (4, N'Phạm Minh Tuấn', N'tuan@gmail.com', N'0909000001')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (5, N'Ngô Thị Mai', N'mai@gmail.com', N'0909000002')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (6, N'Hoàng Anh', N'anh@gmail.com', N'0909000003')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (7, N'Phạm Gia Bảo', N'baopham22@sinhvien.edu.vn', N'0912345678')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (8, N'Nguyễn Thị Lan', N'lannguyen21@sinhvien.edu.vn', N'0987654321')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (9, N'Trần Văn Hùng', N'hungtran@sinhvien.edu.vn', N'0935123456')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (10, N'Lê Minh Thư', N'thule20@sinhvien.edu.vn', N'0903123789')
INSERT [dbo].[Student] ([StudentID], [StudentName], [Email], [Phone]) VALUES (11, N'Hoàng Anh Tuấn', N'tuanhoang@sinhvien.edu.vn', NULL)
SET IDENTITY_INSERT [dbo].[Student] OFF
GO
SET ANSI_PADDING ON
GO
/**\*\*** Object: Index [UQ__BookCode__0A5FFCC7E93EAD05] Script Date: 26/02/2026 8:20:40 CH **\*\***/
ALTER TABLE [dbo].[BookCode] ADD UNIQUE NONCLUSTERED
(
[BookCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/**\*\*** Object: Index [IX_BookFile_BookID] Script Date: 26/02/2026 8:20:40 CH **\*\***/
CREATE NONCLUSTERED INDEX [IX_BookFile_BookID] ON [dbo].[BookFile]
(
[BookID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/**\*\*** Object: Index [IX_BookFile_StaffID] Script Date: 26/02/2026 8:20:40 CH **\*\***/
CREATE NONCLUSTERED INDEX [IX_BookFile_StaffID] ON [dbo].[BookFile]
(
[StaffID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/**\*\*** Object: Index [UQ__Category__8517B2E0833F0940] Script Date: 26/02/2026 8:20:40 CH **\*\***/
ALTER TABLE [dbo].[Category] ADD UNIQUE NONCLUSTERED
(
[CategoryName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/**\*\*** Object: Index [UQ__Publishe__5F0E2249DA2B225E] Script Date: 26/02/2026 8:20:40 CH **\*\***/
ALTER TABLE [dbo].[Publisher] ADD UNIQUE NONCLUSTERED
(
[PublisherName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/**\*\*** Object: Index [UQ__Role__8A2B6160CB5A830E] Script Date: 26/02/2026 8:20:40 CH **\*\***/
ALTER TABLE [dbo].[Role] ADD UNIQUE NONCLUSTERED
(
[RoleName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/**\*\*** Object: Index [UQ_Staff_Username] Script Date: 26/02/2026 8:20:40 CH **\*\***/
ALTER TABLE [dbo].[Staff] ADD CONSTRAINT [UQ_Staff_Username] UNIQUE NONCLUSTERED
(
[Username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/**\*\*** Object: Index [UQ__Student__A9D10534212029C5] Script Date: 26/02/2026 8:20:40 CH **\*\***/
ALTER TABLE [dbo].[Student] ADD UNIQUE NONCLUSTERED
(
[Email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/**\*\*** Object: Index [UKfe0i52si7ybu0wjedj6motiim] Script Date: 26/02/2026 8:20:40 CH **\*\***/
CREATE UNIQUE NONCLUSTERED INDEX [UKfe0i52si7ybu0wjedj6motiim] ON [dbo].[Student]
(
[Email] ASC
)
WHERE ([email] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[BookFile] ADD DEFAULT (sysutcdatetime()) FOR [UploadAt]
GO
ALTER TABLE [dbo].[BookFile] ADD DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [dbo].[Price] ADD DEFAULT ('VND') FOR [Currency]
GO
ALTER TABLE [dbo].[Book] WITH CHECK ADD CONSTRAINT [FK_Book_Category] FOREIGN KEY([CategoryID])
REFERENCES [dbo].[Category] ([CategoryID])
GO
ALTER TABLE [dbo].[Book] CHECK CONSTRAINT [FK_Book_Category]
GO
ALTER TABLE [dbo].[Book] WITH CHECK ADD CONSTRAINT [FK_Book_Publisher] FOREIGN KEY([PublisherID])
REFERENCES [dbo].[Publisher] ([PublisherID])
GO
ALTER TABLE [dbo].[Book] CHECK CONSTRAINT [FK_Book_Publisher]
GO
ALTER TABLE [dbo].[BookAuthor] WITH CHECK ADD CONSTRAINT [FK_BookAuthor_Author] FOREIGN KEY([AuthorID])
REFERENCES [dbo].[Author] ([AuthorID])
GO
ALTER TABLE [dbo].[BookAuthor] CHECK CONSTRAINT [FK_BookAuthor_Author]
GO
ALTER TABLE [dbo].[BookAuthor] WITH CHECK ADD CONSTRAINT [FK_BookAuthor_Book] FOREIGN KEY([BookID])
REFERENCES [dbo].[Book] ([BookID])
GO
ALTER TABLE [dbo].[BookAuthor] CHECK CONSTRAINT [FK_BookAuthor_Book]
GO
ALTER TABLE [dbo].[BookCode] WITH CHECK ADD CONSTRAINT [FK_BookCode_Book] FOREIGN KEY([BookID])
REFERENCES [dbo].[Book] ([BookID])
GO
ALTER TABLE [dbo].[BookCode] CHECK CONSTRAINT [FK_BookCode_Book]
GO
ALTER TABLE [dbo].[BookFile] WITH CHECK ADD CONSTRAINT [FK_BookFile_Book] FOREIGN KEY([BookID])
REFERENCES [dbo].[Book] ([BookID])
GO
ALTER TABLE [dbo].[BookFile] CHECK CONSTRAINT [FK_BookFile_Book]
GO
ALTER TABLE [dbo].[BookFile] WITH CHECK ADD CONSTRAINT [FK_BookFile_Staff] FOREIGN KEY([StaffID])
REFERENCES [dbo].[Staff] ([StaffID])
GO
ALTER TABLE [dbo].[BookFile] CHECK CONSTRAINT [FK_BookFile_Staff]
GO
ALTER TABLE [dbo].[BookPrice] WITH CHECK ADD CONSTRAINT [FK_BookPrice_Book] FOREIGN KEY([BookID])
REFERENCES [dbo].[Book] ([BookID])
GO
ALTER TABLE [dbo].[BookPrice] CHECK CONSTRAINT [FK_BookPrice_Book]
GO
ALTER TABLE [dbo].[BookPrice] WITH CHECK ADD CONSTRAINT [FK_BookPrice_Price] FOREIGN KEY([PriceID])
REFERENCES [dbo].[Price] ([PriceID])
GO
ALTER TABLE [dbo].[BookPrice] CHECK CONSTRAINT [FK_BookPrice_Price]
GO
ALTER TABLE [dbo].[Borrow] WITH CHECK ADD CONSTRAINT [FK_Borrow_Staff] FOREIGN KEY([StaffID])
REFERENCES [dbo].[Staff] ([StaffID])
GO
ALTER TABLE [dbo].[Borrow] CHECK CONSTRAINT [FK_Borrow_Staff]
GO
ALTER TABLE [dbo].[Borrow] WITH CHECK ADD CONSTRAINT [FK_Borrow_Student] FOREIGN KEY([StudentID])
REFERENCES [dbo].[Student] ([StudentID])
GO
ALTER TABLE [dbo].[Borrow] CHECK CONSTRAINT [FK_Borrow_Student]
GO
ALTER TABLE [dbo].[BorrowItem] WITH CHECK ADD CONSTRAINT [FK_BorrowItem_Book] FOREIGN KEY([BookID])
REFERENCES [dbo].[Book] ([BookID])
GO
ALTER TABLE [dbo].[BorrowItem] CHECK CONSTRAINT [FK_BorrowItem_Book]
GO
ALTER TABLE [dbo].[BorrowItem] WITH CHECK ADD CONSTRAINT [FK_BorrowItem_Borrow] FOREIGN KEY([BorrowID])
REFERENCES [dbo].[Borrow] ([BorrowID])
GO
ALTER TABLE [dbo].[BorrowItem] CHECK CONSTRAINT [FK_BorrowItem_Borrow]
GO
ALTER TABLE [dbo].[OrderDetail] WITH CHECK ADD CONSTRAINT [FK_OrderDetail_Book] FOREIGN KEY([BookID])
REFERENCES [dbo].[Book] ([BookID])
GO
ALTER TABLE [dbo].[OrderDetail] CHECK CONSTRAINT [FK_OrderDetail_Book]
GO
ALTER TABLE [dbo].[OrderDetail] WITH CHECK ADD CONSTRAINT [FK_OrderDetail_Order] FOREIGN KEY([OrderID])
REFERENCES [dbo].[Orders] ([OrderID])
GO
ALTER TABLE [dbo].[OrderDetail] CHECK CONSTRAINT [FK_OrderDetail_Order]
GO
ALTER TABLE [dbo].[Orders] WITH CHECK ADD CONSTRAINT [FK_Orders_Staff] FOREIGN KEY([StaffID])
REFERENCES [dbo].[Staff] ([StaffID])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_Staff]
GO
ALTER TABLE [dbo].[Orders] WITH CHECK ADD CONSTRAINT [FK_Orders_Student] FOREIGN KEY([StudentID])
REFERENCES [dbo].[Student] ([StudentID])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_Student]
GO
ALTER TABLE [dbo].[StaffRole] WITH CHECK ADD CONSTRAINT [FK_StaffRole_Role] FOREIGN KEY([RoleID])
REFERENCES [dbo].[Role] ([RoleID])
GO
ALTER TABLE [dbo].[StaffRole] CHECK CONSTRAINT [FK_StaffRole_Role]
GO
ALTER TABLE [dbo].[StaffRole] WITH CHECK ADD CONSTRAINT [FK_StaffRole_Staff] FOREIGN KEY([StaffID])
REFERENCES [dbo].[Staff] ([StaffID])
GO
ALTER TABLE [dbo].[StaffRole] CHECK CONSTRAINT [FK_StaffRole_Staff]
GO
ALTER TABLE [dbo].[Book] WITH CHECK ADD CHECK (([Available]>=(0)))
GO
ALTER TABLE [dbo].[Book] WITH CHECK ADD CHECK (([Quantity]>=(0)))
GO
ALTER TABLE [dbo].[Book] WITH CHECK ADD CONSTRAINT [CK_Book_Available] CHECK (([Available]<=[Quantity]))
GO
ALTER TABLE [dbo].[Book] CHECK CONSTRAINT [CK_Book_Available]
GO
ALTER TABLE [dbo].[BookFile] WITH CHECK ADD CHECK (([FileSize] IS NULL OR [FileSize]>=(0)))
GO
ALTER TABLE [dbo].[BookPrice] WITH CHECK ADD CONSTRAINT [CK_BookPrice_Date] CHECK (([EndDate] IS NULL OR [EndDate]>=[StartDate]))
GO
ALTER TABLE [dbo].[BookPrice] CHECK CONSTRAINT [CK_BookPrice_Date]
GO
ALTER TABLE [dbo].[Borrow] WITH CHECK ADD CHECK (([Status]='Overdue' OR [Status]='Returned' OR [Status]='Borrowing'))
GO
ALTER TABLE [dbo].[Borrow] WITH CHECK ADD CONSTRAINT [CK_Borrow_Date] CHECK (([DueDate]>=[BorrowDate]))
GO
ALTER TABLE [dbo].[Borrow] CHECK CONSTRAINT [CK_Borrow_Date]
GO
ALTER TABLE [dbo].[BorrowItem] WITH CHECK ADD CHECK (([Quantity]>(0)))
GO
ALTER TABLE [dbo].[OrderDetail] WITH CHECK ADD CHECK (([Quantity]>(0)))
GO
ALTER TABLE [dbo].[OrderDetail] WITH CHECK ADD CHECK (([UnitPrice]>(0)))
GO
ALTER TABLE [dbo].[Orders] WITH CHECK ADD CHECK (([TotalAmount]>=(0)))
GO
ALTER TABLE [dbo].[Price] WITH CHECK ADD CHECK (([Amount]>(0)))
GO
USE [master]
GO
ALTER DATABASE [LibraryManager_V2] SET READ_WRITE
GO
