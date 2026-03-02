-- SQL to fix roles for Admin, Staff, and Student
-- Based on the requirement:
-- Admin (Role 1): Manage all
-- Staff (Role 2): Confirm returns and orders
-- Student (Role 8): Borrow or buy

USE [LibraryManager_V2]
GO

-- 1. Ensure Roles exist
IF NOT EXISTS (SELECT 1 FROM [Role] WHERE [RoleID] = 1) INSERT INTO [Role] ([RoleID], [RoleName]) VALUES (1, N'Admin');
IF NOT EXISTS (SELECT 1 FROM [Role] WHERE [RoleID] = 2) INSERT INTO [Role] ([RoleID], [RoleName]) VALUES (2, N'Staff');
IF NOT EXISTS (SELECT 1 FROM [Role] WHERE [RoleID] = 8) INSERT INTO [Role] ([RoleID], [RoleName]) VALUES (8, N'Student');

-- 2. Fix 'admin' account (StaffID 10)
UPDATE [Staff] SET [Username] = 'admin', [Password] = '123' WHERE [StaffID] = 10;
DELETE FROM [StaffRole] WHERE [StaffID] = 10;
INSERT INTO [StaffRole] ([StaffID], [RoleID]) VALUES (10, 1);

-- 3. Fix 'staff_1' account (StaffID 1)
UPDATE [Staff] SET [Username] = 'staff_1', [Password] = '123' WHERE [StaffID] = 1;
DELETE FROM [StaffRole] WHERE [StaffID] = 1;
INSERT INTO [StaffRole] ([StaffID], [RoleID]) VALUES (1, 2);

-- 4. Fix 'student_1' account (StaffID 11)
-- Currently it might have Role 1, we must change it to Role 8
IF NOT EXISTS (SELECT 1 FROM [Staff] WHERE [StaffID] = 11)
    INSERT INTO [Staff] ([StaffID], [StaffName], [Username], [Password]) VALUES (11, N'Student Account', N'student_1', N'123');
ELSE
    UPDATE [Staff] SET [Username] = 'student_1', [Password] = '123' WHERE [StaffID] = 11;

DELETE FROM [StaffRole] WHERE [StaffID] = 11;
INSERT INTO [StaffRole] ([StaffID], [RoleID]) VALUES (11, 8);

GO
