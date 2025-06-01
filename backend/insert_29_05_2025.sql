-- Listage des données de la table building.users : ~0 rows (environ)
INSERT INTO `users` (`id`, `first_name`, `last_name`, `email`, `password`, `phone`, `role`, `birthday`, `gender`, `image`, `is_active`, `is_deleted`, `registration_date`, `joined_date`, `created_date`, `updated_date`, `username`) VALUES
  (1, 'admin', 'admin', 'admin@gmail.com', '$2a$10$mFO/5bu5du/ys9muLzTxTuROjZWPhA2PQXWgMrPdIDRc0e5FsToNy', '11111111', 'ADMIN', NULL, NULL, NULL, 0, 0, '2025-05-25 17:55:04', NULL, NULL, NULL, 'admin@gmail.com');

-- Listage des données de la table building.housting_units : ~3 rows (environ)
INSERT INTO `housting_units` (`id`, `number`, `floor`, `area`, `address`, `type`, `tenant_id`, `created_date`, `updated_date`, `price`) VALUES
                                                                                                                                          (1, 'B 100', 20, 20.00, 'test', 'Chambre', null, '2025-05-28 23:59:18', NULL, 1000.00),
                                                                                                                                          (2, 'S 222', 200, 1000.00, 'TEST', 'Studio', NULL, NULL, NULL, 2000.00),
                                                                                                                                          (3, 'A 11', 20, 30.00, 'twest', 'Appartement', NULL, NULL, NULL, 100000.00);

-- Listage des données de la table building.tenants : ~0 rows (environ)
INSERT INTO `tenants` (`id`, `user_id`, `housing_unit_id`, `move_in_date`, `move_out_date`, `created_date`, `updated_date`, `security_deposit`, `housting_price`) VALUES
  (14, 1, 1, '2025-05-28 23:00:00', '2026-05-28 23:00:00', '2025-05-28 23:59:18', NULL, 3400.00, NULL);

INSERT INTO `housting_units` (`id`, `number`, `floor`, `area`, `address`, `type`, `tenant_id`, `created_date`, `updated_date`, `price`) VALUES
                                                                                                                                          (1, 'B 100', 20, 20.00, 'test', 'Chambre', 14, '2025-05-28 23:59:18', NULL, 1000.00);

-- Listage des données de la table building.rents : ~0 rows (environ)

-- Listage des données de la table building.services : ~15 rows (environ)
INSERT INTO `services` (`id`, `code`, `name`, `type`, `description`, `billing_mode`, `price`, `created_date`, `updated_date`, `is_active`) VALUES
	(1, 'ww', 'loyer', 'PERIODIC', 'dsds', 'monthly', NULL, NULL, NULL, 1),
	(10, '0001', 'laverie', 'PERIODIC_WITH_OPTIONS', 'tes', 'monthly', 0.00, '2025-05-19 19:18:11', '2025-05-19 19:18:11', 1),
	(17, NULL, 'Internet Service', 'PERIODIC', NULL, 'monthly', 50.00, NULL, NULL, 1),
	(18, NULL, 'Cleaning Service', 'PERIODIC', NULL, 'monthly', 30.00, NULL, NULL, 1),
	(19, 'b', 'Gym Membership', 'PERIODIC_WITH_OPTIONS', 'b', 'Monthly', 20.00, NULL, '2025-05-29 01:40:48', 1),
	(20, '2', 'Streaming Service', 'PERIODIC_WITH_OPTIONS', '2', 'monthly', 10.00, '2025-05-20 14:20:19', '2025-05-29 01:42:10', 1),
	(21, NULL, 'Basic Water Supply Service', 'PERIODIC', NULL, 'monthly', 25.00, '2025-05-20 14:20:19', NULL, 1),
	(22, NULL, 'Parking Space Rental', 'PERIODIC', NULL, 'monthly', 50.00, '2025-05-20 14:20:19', NULL, 1),
	(23, NULL, 'Cleaning Service', 'PERIODIC', NULL, 'monthly', 30.00, '2025-05-20 14:20:19', NULL, 1),
	(24, NULL, 'Swimming Pool Access', 'PERIODIC', NULL, 'seasonal', 100.00, '2025-05-20 14:20:19', NULL, 1),
	(25, 'w', 'Home Security Service', 'PERIODIC_WITH_OPTIONS', 'w', 'monthly', 40.00, '2025-05-20 14:20:19', '2025-05-29 01:44:09', 1),
	(26, NULL, 'Premium Internet Service', 'PERIODIC', NULL, 'monthly', 60.00, '2025-05-20 14:20:19', NULL, 1),
	(27, 'q', 'Home Cleaning Service', 'OPTIONS', 'q', NULL, 50.00, '2025-05-20 14:20:19', '2025-05-29 01:45:40', 1),
	(28, NULL, 'Magazine Subscription', 'PERIODIC', NULL, 'monthly', 10.00, '2025-05-20 14:20:19', NULL, 1),
	(29, NULL, 'Gym Membership', 'PERIODIC', NULL, 'monthly', 20.00, '2025-05-20 14:20:19', NULL, 1);

-- Listage des données de la table building.service_options : ~0 rows (environ)
INSERT INTO `service_options` (`id`, `service_id`, `name`, `price`, `max_quantity`, `is_active`, `created_date`, `updated_date`, `quantity`) VALUES
	(1, 19, 'Personal Trainer', 15.00, 2, 1, NULL, '2025-05-29 01:40:48', NULL),
	(2, 19, 'Access to Sauna', 10.00, 1, 1, NULL, '2025-05-29 01:40:48', NULL),
	(3, 20, 'Additional Screens', 5.00, 2, 1, NULL, '2025-05-29 01:42:10', NULL),
	(4, 20, 'Offline Downloads', 3.00, 1, 1, NULL, '2025-05-29 01:42:10', NULL),
	(5, 25, 'Additional Cameras', 10.00, 3, 1, NULL, '2025-05-29 01:44:09', NULL),
	(6, 25, '24/7 Monitoring', 20.00, 1, 1, NULL, '2025-05-29 01:44:09', NULL),
	(7, 27, 'Carpet Cleaning', 15.00, 1, 1, NULL, '2025-05-29 01:45:40', NULL),
	(8, 27, 'Deep Cleaning', 20.00, 1, 1, NULL, '2025-05-29 01:45:40', NULL);

-- Listage des données de la table building.invoices : ~1 rows (environ)
INSERT INTO `invoices` (`id`, `user_id`, `modify_id`, `type`, `number`, `month`, `year`, `amount`, `payment_date`, `created_date`, `updated_date`, `status`, `tenant_id`) VALUES
                                                                                                                                                                            (10, 1, 1, 'subscription', 'INV-2025-05-00015', 5, 2025, 12000.00, '2025-05-29 00:55:06', '2025-05-28 23:59:18', '2025-05-29 00:55:06', 'PARTIAL PAID', 14),
                                                                                                                                                                            (11, 1, 1, 'subscription', 'INV-2025-05-00016', NULL, NULL, 600.00, NULL, '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING', NULL),
                                                                                                                                                                            (12, 1, 1, 'subscription', 'INV-2025-05-00017', NULL, NULL, 30.00, NULL, '2025-05-29 01:35:04', '2025-05-29 01:35:04', 'PENDING', NULL),
                                                                                                                                                                            (13, 1, 1, 'subscription', 'INV-2025-05-00018', NULL, NULL, 360.00, NULL, '2025-05-29 01:47:25', '2025-05-29 01:47:25', 'PENDING', NULL);

-- Listage des données de la table building.subscriptions : ~1 rows (environ)
INSERT INTO `subscriptions` (`id`, `tenant_id`, `update_by`, `invoice_id`, `service_id`, `total_price`, `start_date`, `end_date`, `subscript_number`, `created_date`, `updated_date`, `status`, `modify_by`) VALUES
	(10, 14, NULL, 10, NULL, 12000.00, '2025-05-28 23:00:00', '2026-05-28 23:00:00', 12, '2025-05-28 23:59:18', '2025-05-29 00:55:06', 'ACTIVE', 1),
	(11, 14, NULL, 11, NULL, 600.00, '2025-05-29 01:01:55', '2025-06-29 01:01:55', NULL, '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'ACTIVE', 1),
	(12, 14, NULL, 12, NULL, 30.00, '2025-05-29 01:35:04', '2025-06-29 01:35:04', NULL, '2025-05-29 01:35:04', '2025-05-29 01:35:04', 'ACTIVE', 1),
	(13, 14, NULL, 13, NULL, 360.00, '2025-05-29 01:47:25', '2025-06-29 01:47:25', NULL, '2025-05-29 01:47:25', '2025-05-29 01:47:25', 'ACTIVE', 1);

-- Listage des données de la table building.subscription_services : ~1 rows (environ)
INSERT INTO `subscription_services` (`id`, `subscription_id`, `service_id`, `quantity`, `price`, `total_price`, `subscript_number`, `start_date`, `end_date`, `created_date`, `updated_date`, `billing_cycle_id`, `amount_due`) VALUES
                                                                                                                                                                                                                                  (10, 10, 1, 12, 1000.00, 12000.00, 12, '2025-05-28 23:00:00', '2026-05-28 23:00:00', '2025-05-28 23:59:18', '2025-05-28 23:59:18', NULL, 12000.00),
                                                                                                                                                                                                                                  (11, 11, 17, 12, 50.00, 600.00, 12, '2025-05-28 23:00:00', '2026-05-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', NULL, 600.00),
                                                                                                                                                                                                                                  (12, 12, 18, 1, 30.00, 30.00, 1, '2025-05-28 23:00:00', '2025-06-28 23:00:00', '2025-05-29 01:35:04', '2025-05-29 01:35:04', NULL, 30.00),
                                                                                                                                                                                                                                  (13, 13, 19, 6, 20.00, 120.00, 6, '2025-05-28 23:00:00', '2025-11-28 23:00:00', '2025-05-29 01:47:25', '2025-05-29 01:47:25', NULL, 360.00);

-- Listage des données de la table building.subscription_options : ~0 rows (environ)
INSERT INTO `subscription_options` (`id`, `subscription_service_id`, `subscription_id`, `option_id`, `quantity`, `price`, `created_date`, `updated_date`, `amount_due`) VALUES
	(1, 13, 13, 1, 2, 15.00, '2025-05-29 01:47:25', '2025-05-29 01:47:25', 180.00),
	(2, 13, 13, 2, 1, 10.00, '2025-05-29 01:47:25', '2025-05-29 01:47:25', 60.00);

-- Listage des données de la table building.billing_cycles : ~12 rows (environ)
INSERT INTO `billing_cycles` (`id`, `subscription_id`, `subscription_services_id`, `amount_due`, `period_start`, `period_end`, `created_date`, `updated_date`, `status`) VALUES
                                                                                                                                                                           (109, 10, 10, 1000.00, '2025-05-28 23:00:00', '2025-06-28 23:00:00', '2025-05-28 23:59:18', '2025-05-28 23:59:18', 'PAID'),
                                                                                                                                                                           (110, 10, 10, 1000.00, '2025-06-28 23:00:00', '2025-07-28 23:00:00', '2025-05-28 23:59:18', '2025-05-28 23:59:18', 'PAID'),
                                                                                                                                                                           (111, 10, 10, 1000.00, '2025-07-28 23:00:00', '2025-08-28 23:00:00', '2025-05-28 23:59:18', '2025-05-28 23:59:18', 'PAID'),
                                                                                                                                                                           (112, 10, 10, 1000.00, '2025-08-28 23:00:00', '2025-09-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:53:43', 'PAID'),
                                                                                                                                                                           (113, 10, 10, 1000.00, '2025-09-28 23:00:00', '2025-10-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:53:56', 'PAID'),
                                                                                                                                                                           (114, 10, 10, 1000.00, '2025-10-28 23:00:00', '2025-11-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:55:06', 'PARTIAL_PAID'),
                                                                                                                                                                           (115, 10, 10, 1000.00, '2025-11-28 23:00:00', '2025-12-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:51:47', 'PENDING'),
                                                                                                                                                                           (116, 10, 10, 1000.00, '2025-12-28 23:00:00', '2026-01-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:51:47', 'PENDING'),
                                                                                                                                                                           (117, 10, 10, 1000.00, '2026-01-28 23:00:00', '2026-02-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:51:47', 'PENDING'),
                                                                                                                                                                           (118, 10, 10, 1000.00, '2026-02-28 23:00:00', '2026-03-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:51:47', 'PENDING'),
                                                                                                                                                                           (119, 10, 10, 1000.00, '2026-03-28 23:00:00', '2026-04-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:51:47', 'PENDING'),
                                                                                                                                                                           (120, 10, 10, 1000.00, '2026-04-28 23:00:00', '2026-05-28 23:00:00', '2025-05-28 23:59:18', '2025-05-29 00:51:47', 'PENDING'),
                                                                                                                                                                           (121, 11, 11, 50.00, '2025-05-28 23:00:00', '2025-06-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (122, 11, 11, 50.00, '2025-06-28 23:00:00', '2025-07-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (123, 11, 11, 50.00, '2025-07-28 23:00:00', '2025-08-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (124, 11, 11, 50.00, '2025-08-28 23:00:00', '2025-09-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (125, 11, 11, 50.00, '2025-09-28 23:00:00', '2025-10-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (126, 11, 11, 50.00, '2025-10-28 23:00:00', '2025-11-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (127, 11, 11, 50.00, '2025-11-28 23:00:00', '2025-12-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (128, 11, 11, 50.00, '2025-12-28 23:00:00', '2026-01-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (129, 11, 11, 50.00, '2026-01-28 23:00:00', '2026-02-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (130, 11, 11, 50.00, '2026-02-28 23:00:00', '2026-03-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (131, 11, 11, 50.00, '2026-03-28 23:00:00', '2026-04-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (132, 11, 11, 50.00, '2026-04-28 23:00:00', '2026-05-28 23:00:00', '2025-05-29 01:01:55', '2025-05-29 01:01:55', 'PENDING'),
                                                                                                                                                                           (133, 12, 12, 30.00, '2025-05-28 23:00:00', '2025-06-28 23:00:00', '2025-05-29 01:35:04', '2025-05-29 01:35:04', 'PENDING'),
                                                                                                                                                                           (134, 13, 13, 60.00, '2025-05-28 23:00:00', '2025-06-28 23:00:00', '2025-05-29 01:47:25', '2025-05-29 01:47:25', 'PENDING'),
                                                                                                                                                                           (135, 13, 13, 100.00, '2025-06-28 23:00:00', '2025-07-28 23:00:00', '2025-05-29 01:47:25', '2025-05-29 01:47:25', 'PENDING'),
                                                                                                                                                                           (136, 13, 13, 140.00, '2025-07-28 23:00:00', '2025-08-28 23:00:00', '2025-05-29 01:47:25', '2025-05-29 01:47:25', 'PENDING'),
                                                                                                                                                                           (137, 13, 13, 60.00, '2025-08-28 23:00:00', '2025-09-28 23:00:00', '2025-05-29 01:47:25', '2025-05-29 01:47:25', 'PENDING'),
                                                                                                                                                                           (138, 13, 13, 0.00, '2025-09-28 23:00:00', '2025-10-28 23:00:00', '2025-05-29 01:47:25', '2025-05-29 01:47:25', 'PENDING'),
                                                                                                                                                                           (139, 13, 13, 0.00, '2025-10-28 23:00:00', '2025-11-28 23:00:00', '2025-05-29 01:47:25', '2025-05-29 01:47:25', 'PENDING');

-- Listage des données de la table building.payments : ~1 rows (environ)
INSERT INTO `payments` (`id`, `tenant_id`, `payment_date`, `total_amount`, `created_date`, `updated_date`, `payment_method`) VALUES
                                                                                                                               (22, 14, '2025-05-28 23:59:18', 3400.00, '2025-05-28 23:59:18', '2025-05-28 23:59:18', 'CASH'),
                                                                                                                               (32, 14, '2025-05-29 00:53:12', 2500.00, '2025-05-29 00:53:12', '2025-05-29 00:53:12', 'CASH');

-- Listage des données de la table building.payment_lines : ~8 rows (environ)
INSERT INTO `payment_lines` (`id`, `payment_id`, `billing_cycle_id`, `created_date`, `updated_date`, `amount_paid`) VALUES
                                                                                                                      (121, 22, 109, '2025-05-28 23:59:18', '2025-05-28 23:59:18', 1000.00),
                                                                                                                      (122, 22, 110, '2025-05-28 23:59:18', '2025-05-28 23:59:18', 1000.00),
                                                                                                                      (123, 22, 111, '2025-05-28 23:59:18', '2025-05-28 23:59:18', 1000.00),
                                                                                                                      (124, 22, 112, '2025-05-28 23:59:18', '2025-05-28 23:59:43', 400.00),
                                                                                                                      (196, 32, 112, '2025-05-29 00:53:12', '2025-05-29 00:53:43', 600.00),
                                                                                                                      (197, 32, 113, '2025-05-29 00:53:56', '2025-05-29 00:53:56', 1000.00),
                                                                                                                      (198, 32, 114, '2025-05-29 00:55:06', '2025-05-29 00:55:06', 900.00);

-- Listage des données de la table building.invoice_counter : ~0 rows (environ)
INSERT INTO `invoice_counter` (`id`, `year`, `counter`) VALUES
  (1, 2025, 18);

