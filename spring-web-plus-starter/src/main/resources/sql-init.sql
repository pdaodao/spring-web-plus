insert into sys_user(id, username, salt, password, nickname)
values (1, 'admin', 'sa', '286643d82634d8b70fd5ac03c2a4500db62a17f94a9c30787708e34795d97423', '管理员');

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (1, '系统管理', 0, 1, '', '/system', '', '', 'ele-setting', 99, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (2, '用户管理', 1, 2, 'sys:user:list', '/system/user', 'system/user/index', NULL, 'ele-user', 1, 1, 0, 1
);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (3, '角色管理', 1, 2, 'sys:role:list', '/system/role', 'system/role/index', NULL, 'local-role', 2, 1, 1,
1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (4, '菜单管理', 1, 2, 'sys:menu:tree-list', '/system/menu', 'system/menu/index', NULL, 'local-menu', 3,
1, 1, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (5, '添加用户', 2, 3, 'sys:user:add', NULL, NULL, NULL, NULL, 1, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (6, '修改用户', 2, 3, 'sys:user:update', NULL, NULL, NULL, NULL, 2, 1, 0, 1);


INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (7, '删除用户', 2, 3, 'sys:user:delete', NULL, NULL, NULL, NULL, 3, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (8, '用户详情', 2, 3, 'sys:user:info', NULL, NULL, NULL, NULL, 4, 1, 0, 1);


INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (9, '重置用户密码', 2, 3, 'sys:user:reset-password', NULL, NULL, NULL, NULL, 6, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (10, '添加角色', 3, 3, 'sys:role:add', NULL, NULL, NULL, NULL, 1, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (11, '修改角色', 3, 3, 'sys:role:update', NULL, NULL, NULL, NULL, 2, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (12, '删除角色', 3, 3, 'sys:role:delete', NULL, NULL, NULL, NULL, 3, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (13, '角色详情', 3, 3, 'sys:role:info', NULL, NULL, NULL, NULL, 4, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (14, '设置角色权限', 3, 3, 'sys:role:set-role-menus', NULL, NULL, NULL, NULL, 5, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (15, '添加菜单', 4, 3, 'sys:menu:add', NULL, NULL, NULL, NULL, 1, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (16, '修改菜单', 4, 3, 'sys:menu:update', NULL, NULL, NULL, NULL, 2, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (17, '删除菜单', 4, 3, 'sys:menu:delete', NULL, NULL, NULL, NULL, 3, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (18, '菜单详情', 4, 3, 'sys:menu:info', NULL, NULL, NULL, NULL, 4, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (19, '日志列表', 1, 3, 'sys:log:list', NULL, NULL, NULL, NULL, 3, 1, 0, 1);


INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (20, '日志详情', 19, 3, 'sys:log:info', NULL, NULL, NULL, NULL, 2, 0, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (21, '字典管理', 1, 2, 'dict', '/system/dict', 'system/dict/index', NULL, 'ele-notebook', 3, 1,
0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES (22, '系统配置', 1, 2, 'sys:config:manager', '/system/config', 'system/config/index', NULL,
'ele-cpu',
4, 1, 0, 1);