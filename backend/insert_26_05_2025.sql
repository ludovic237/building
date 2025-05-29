
-- Listage des données de la table building.billing_cycles : ~0 rows (environ)
INSERT INTO `billing_cycles` (`id`, `subscription_id`, `amount_due`, `period_start`, `period_end`, `created_date`, `updated_date`, `status`, `subscription_services_id`) VALUES
	(14, 3, 1000.00, '2025-05-26 23:00:00', '2025-06-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3),
	(15, 3, 1000.00, '2025-06-26 23:00:00', '2025-07-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3),
	(16, 3, 1000.00, '2025-07-26 23:00:00', '2025-08-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3),
	(17, 3, 1000.00, '2025-08-26 23:00:00', '2025-09-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3),
	(18, 3, 1000.00, '2025-09-26 23:00:00', '2025-10-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3),
	(19, 3, 1000.00, '2025-10-26 23:00:00', '2025-11-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3),
	(20, 3, 1000.00, '2025-11-26 23:00:00', '2025-12-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3),
	(21, 3, 1000.00, '2025-12-26 23:00:00', '2026-01-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PARTIAL_PAID', 3),
	(22, 3, 1000.00, '2026-01-26 23:00:00', '2026-02-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PENDING', 3),
	(23, 3, 1000.00, '2026-02-26 23:00:00', '2026-03-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PENDING', 3),
	(24, 3, 1000.00, '2026-03-26 23:00:00', '2026-04-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PENDING', 3),
	(25, 3, 1000.00, '2026-04-26 23:00:00', '2026-05-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PENDING', 3),
	(26, 4, 50.00, '2025-05-25 23:00:00', '2025-06-25 23:00:00', '2025-05-26 02:16:27', '2025-05-26 02:16:27', 'PENDING', 4),
	(27, 4, 50.00, '2025-06-25 23:00:00', '2025-07-25 23:00:00', '2025-05-26 02:16:27', '2025-05-26 02:16:27', 'PENDING', 4),
	(28, 4, 50.00, '2025-07-25 23:00:00', '2025-08-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(29, 4, 50.00, '2025-08-25 23:00:00', '2025-09-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(30, 4, 50.00, '2025-09-25 23:00:00', '2025-10-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(31, 4, 50.00, '2025-10-25 23:00:00', '2025-11-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(32, 4, 50.00, '2025-11-25 23:00:00', '2025-12-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(33, 4, 50.00, '2025-12-25 23:00:00', '2026-01-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(34, 4, 50.00, '2026-01-25 23:00:00', '2026-02-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(35, 4, 50.00, '2026-02-25 23:00:00', '2026-03-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(36, 4, 50.00, '2026-03-25 23:00:00', '2026-04-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4),
	(37, 4, 50.00, '2026-04-25 23:00:00', '2026-05-25 23:00:00', '2025-05-26 02:16:28', '2025-05-26 02:16:28', 'PENDING', 4);

-- Listage des données de la table building.documents : ~0 rows (environ)

-- Listage des données de la table building.housting_units : ~0 rows (environ)
INSERT INTO `housting_units` (`id`, `number`, `floor`, `area`, `address`, `type`, `tenant_id`, `created_date`, `updated_date`, `price`) VALUES
	(1, 'B 100', 20, 20.00, 'test', 'Chambre', Null, '2025-05-26 01:46:24', NULL, 1000.00),
	(2, 'S 222', 200, 1000.00, 'TEST', 'Studio', NULL, NULL, NULL, 2000.00),
	(3, 'A 11', 20, 30.00, 'twest', 'Appartement', NULL, NULL, NULL, 100000.00);

-- Listage des données de la table building.invoices : ~0 rows (environ)
INSERT INTO `invoices` (`id`, `user_id`, `modify_id`, `type`, `number`, `month`, `year`, `amount`, `payment_date`, `created_date`, `updated_date`, `status`, `tenant_id`) VALUES
	(3, 1, 1, 'subscription', 'INV-2025-05-00004', 5, 2025, 12000.00, '2025-05-26 01:46:24', '2025-05-26 01:46:24', NULL, 'PARTIAL_PAID', 4),
	(4, 1, 1, 'subscription', 'INV-2025-05-00005', NULL, NULL, 600.00, NULL, '2025-05-26 02:16:27', '2025-05-26 02:16:27', 'PENDING', NULL);

-- Listage des données de la table building.invoice_counter : ~0 rows (environ)
INSERT INTO `invoice_counter` (`id`, `year`, `counter`) VALUES
	(1, 2025, 5);

-- Listage des données de la table building.issues : ~0 rows (environ)

-- Listage des données de la table building.payments : ~0 rows (environ)
INSERT INTO `payments` (`id`, `tenant_id`, `payment_date`, `total_amount`, `created_date`, `updated_date`, `payment_method`) VALUES
	(3, 4, '2025-05-26 01:46:24', 7400.00, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'CASH');

-- Listage des données de la table building.payment_lines : ~0 rows (environ)
INSERT INTO `payment_lines` (`id`, `payment_id`, `billing_cycle_id`, `created_date`, `updated_date`, `amount_paid`) VALUES
	(14, 3, 14, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00),
	(15, 3, 15, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00),
	(16, 3, 16, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00),
	(17, 3, 17, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00),
	(18, 3, 18, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00),
	(19, 3, 19, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00),
	(20, 3, 20, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00),
	(21, 3, 21, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 400.00),
	(22, NULL, 26, '2025-05-26 02:16:27', '2025-05-26 02:16:27', 0.00),
	(23, NULL, 27, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(24, NULL, 28, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(25, NULL, 29, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(26, NULL, 30, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(27, NULL, 31, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(28, NULL, 32, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(29, NULL, 33, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(30, NULL, 34, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(31, NULL, 35, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(32, NULL, 36, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00),
	(33, NULL, 37, '2025-05-26 02:16:28', '2025-05-26 02:16:28', 0.00);

-- Listage des données de la table building.rents : ~0 rows (environ)

-- Listage des données de la table building.services : ~0 rows (environ)
INSERT INTO `services` (`id`, `code`, `name`, `type`, `description`, `billing_mode`, `price`, `created_date`, `updated_date`, `is_active`) VALUES
	(1, 'ww', 'loyer', 'PERIODIC', 'dsds', 'monthly', NULL, NULL, NULL, 1),
	(10, '0001', 'laverie', 'PERIODIC_WITH_OPTIONS', 'tes', 'monthly', 0.00, '2025-05-19 19:18:11', '2025-05-19 19:18:11', 1),
	(17, NULL, 'Internet Service', 'PERIODIC', NULL, 'monthly', 50.00, NULL, NULL, 1),
	(18, NULL, 'Cleaning Service', 'PERIODIC', NULL, 'monthly', 30.00, NULL, NULL, 1),
	(19, NULL, 'Gym Membership', 'PERIODIC_WITH_OPTIONS', NULL, 'monthly', 20.00, NULL, NULL, 1),
	(20, NULL, 'Streaming Service', 'PERIODIC_WITH_OPTIONS', NULL, 'monthly', 10.00, '2025-05-20 14:20:19', NULL, 1),
	(21, NULL, 'Basic Water Supply Service', 'PERIODIC', NULL, 'monthly', 25.00, '2025-05-20 14:20:19', NULL, 1),
	(22, NULL, 'Parking Space Rental', 'PERIODIC', NULL, 'monthly', 50.00, '2025-05-20 14:20:19', NULL, 1),
	(23, NULL, 'Cleaning Service', 'PERIODIC', NULL, 'monthly', 30.00, '2025-05-20 14:20:19', NULL, 1),
	(24, NULL, 'Swimming Pool Access', 'PERIODIC', NULL, 'seasonal', 100.00, '2025-05-20 14:20:19', NULL, 1),
	(25, NULL, 'Home Security Service', 'PERIODIC_WITH_OPTIONS', NULL, 'monthly', 40.00, '2025-05-20 14:20:19', NULL, 1),
	(26, NULL, 'Premium Internet Service', 'PERIODIC', NULL, 'monthly', 60.00, '2025-05-20 14:20:19', NULL, 1),
	(27, NULL, 'Home Cleaning Service', 'OPTIONS', NULL, 'per-session', 50.00, '2025-05-20 14:20:19', NULL, 1),
	(28, NULL, 'Magazine Subscription', 'PERIODIC', NULL, 'monthly', 10.00, '2025-05-20 14:20:19', NULL, 1),
	(29, NULL, 'Gym Membership', 'PERIODIC', NULL, 'monthly', 20.00, '2025-05-20 14:20:19', NULL, 1);

-- Listage des données de la table building.service_options : ~0 rows (environ)

-- Listage des données de la table building.service_usage : ~0 rows (environ)

-- Listage des données de la table building.subscriptions : ~0 rows (environ)
INSERT INTO `subscriptions` (`id`, `tenant_id`, `update_by`, `invoice_id`, `service_id`, `total_price`, `start_date`, `end_date`, `subscript_number`, `created_date`, `updated_date`, `status`, `modify_by`) VALUES
	(3, 4, NULL, 3, NULL, 12000.00, '2025-05-26 23:00:00', '2026-05-26 23:00:00', 12, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'ACTIVE', 1),
	(4, 4, NULL, 4, NULL, 600.00, '2025-05-26 02:16:27', '2025-06-26 02:16:27', NULL, '2025-05-26 02:16:27', '2025-05-26 02:16:27', 'ACTIVE', 1);

-- Listage des données de la table building.subscription_options : ~0 rows (environ)

-- Listage des données de la table building.subscription_services : ~0 rows (environ)
INSERT INTO `subscription_services` (`id`, `subscription_id`, `service_id`, `quantity`, `price`, `total_price`, `subscript_number`, `start_date`, `end_date`, `created_date`, `updated_date`, `billing_cycle_id`, `amount_due`) VALUES
	(3, 3, 1, 12, 1000.00, 12000.00, 12, '2025-05-26 23:00:00', '2026-05-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', NULL, 12000.00),
	(4, 4, 17, 12, 50.00, 600.00, 12, '2025-05-25 23:00:00', '2026-05-25 23:00:00', '2025-05-26 02:16:27', '2025-05-26 02:16:27', NULL, 600.00);

-- Listage des données de la table building.tenants : ~0 rows (environ)
INSERT INTO `tenants` (`id`, `user_id`, `housing_unit_id`, `move_in_date`, `move_out_date`, `created_date`, `updated_date`, `security_deposit`, `housting_price`) VALUES
	(4, 1, 1, '2025-05-26 23:00:00', '2026-05-26 23:00:00', '2025-05-26 01:46:24', NULL, 7400.00, NULL);

-- Listage des données de la table building.users : ~0 rows (environ)
INSERT INTO `users` (`id`, `first_name`, `last_name`, `email`, `password`, `phone`, `role`, `birthday`, `gender`, `image`, `is_active`, `is_deleted`, `registration_date`, `joined_date`, `created_date`, `updated_date`, `username`) VALUES
	(1, 'admin', 'admin', 'admin@gmail.com', '$2a$10$mFO/5bu5du/ys9muLzTxTuROjZWPhA2PQXWgMrPdIDRc0e5FsToNy', '11111111', 'ADMIN', NULL, NULL, NULL, 0, 0, '2025-05-25 17:55:04', NULL, NULL, NULL, 'admin@gmail.com');
