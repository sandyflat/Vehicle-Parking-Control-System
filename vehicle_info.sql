-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 10, 2024 at 06:28 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vehicle_info`
--

-- --------------------------------------------------------

--
-- Table structure for table `parking_records`
--

CREATE TABLE `parking_records` (
  `id` int(11) NOT NULL,
  `plate_no` varchar(20) NOT NULL,
  `vehicle_type` varchar(10) NOT NULL,
  `entry_time` datetime NOT NULL,
  `exit_time` datetime DEFAULT NULL,
  `parking_charge` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `parking_records`
--

INSERT INTO `parking_records` (`id`, `plate_no`, `vehicle_type`, `entry_time`, `exit_time`, `parking_charge`) VALUES
(5, 'abcde', 'car', '2023-10-01 11:24:57', '2023-10-01 11:26:13', 0.08),
(7, 'asdfadf', 'car', '2023-10-01 20:34:00', '2023-10-01 20:34:11', 0.00),
(8, 'adfasfdasf', 'bike', '2023-10-01 20:35:09', '2023-10-01 20:35:17', 0.00),
(9, '1212121', 'car', '2023-10-01 20:36:02', '2023-10-01 20:36:11', 0.00),
(10, 'hellow', 'car', '2023-10-01 21:31:13', '2023-10-01 21:31:23', 0.00),
(11, 'uiop', 'car', '2023-10-01 21:34:41', '2023-10-01 21:34:48', 0.00),
(30, 'hellow', 'car', '2023-10-07 17:33:26', '2023-10-07 17:33:32', 0.00),
(31, 'hellow', 'car', '2023-10-07 17:35:03', '2023-10-07 17:35:11', 0.00),
(32, 'testing3', 'car', '2023-10-07 17:39:57', '2023-10-07 17:40:23', 0.00),
(33, 'testing23', 'bike', '2023-10-08 08:52:04', '2023-10-08 08:53:38', 0.08);

-- --------------------------------------------------------

--
-- Table structure for table `user_info`
--

CREATE TABLE `user_info` (
  `id` int(11) NOT NULL,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_info`
--

INSERT INTO `user_info` (`id`, `username`, `password`) VALUES
(8, 'sandip', 'sandip@123');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `parking_records`
--
ALTER TABLE `parking_records`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_info`
--
ALTER TABLE `user_info`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `parking_records`
--
ALTER TABLE `parking_records`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `user_info`
--
ALTER TABLE `user_info`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
