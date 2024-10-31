insert into sys_user(id, username, salt, password, nickname)
values ('1', 'admin', 'sa', '286643d82634d8b70fd5ac03c2a4500db62a17f94a9c30787708e34795d97423', '管理员');

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('11', '系统管理', '0', 1, '', '/system', '', '', 'ele-setting', 99, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('1101', '用户管理', '11', 2, 'sys:user:list', '/system/user', 'system/user/index', NULL, 'ele-user', 1, 1, 0, 1
);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('1102', '角色管理', '11', 2, 'sys:role:list', '/system/role', 'system/role/index', NULL, 'local-role', 2, 1, 1,
1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('1103', '菜单管理', '11', 2, 'sys:menu:tree-list', '/system/menu', 'system/menu/index', NULL, 'local-menu', 3,
1, 1, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110101', '添加用户', '1101', 3, 'sys:user:add', NULL, NULL, NULL, NULL, 1, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110102', '修改用户', '1101', 3, 'sys:user:update', NULL, NULL, NULL, NULL, 2, 1, 0, 1);


INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110103', '删除用户', '1101', 3, 'sys:user:delete', NULL, NULL, NULL, NULL, 3, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110104', '用户详情', '1101', 3, 'sys:user:info', NULL, NULL, NULL, NULL, 4, 1, 0, 1);


INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110106', '重置用户密码', '1101', 3, 'sys:user:reset-password', NULL, NULL, NULL, NULL, 6, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110201', '添加角色', '1102', 3, 'sys:role:add', NULL, NULL, NULL, NULL, 1, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110202', '修改角色', '1102', 3, 'sys:role:update', NULL, NULL, NULL, NULL, 2, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110203', '删除角色', '1102', 3, 'sys:role:delete', NULL, NULL, NULL, NULL, 3, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110204', '角色详情', '1102', 3, 'sys:role:info', NULL, NULL, NULL, NULL, 4, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110207', '设置角色权限', '1102', 3, 'sys:role:set-role-menus', NULL, NULL, NULL, NULL, 5, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110301', '添加菜单', '1103', 3, 'sys:menu:add', NULL, NULL, NULL, NULL, 1, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110302', '修改菜单', '1103', 3, 'sys:menu:update', NULL, NULL, NULL, NULL, 2, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110303', '删除菜单', '1103', 3, 'sys:menu:delete', NULL, NULL, NULL, NULL, 3, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('110304', '菜单详情', '1103', 3, 'sys:menu:info', NULL, NULL, NULL, NULL, 4, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('11050101', '日志详情', '110501', 3, 'sys:log:info', NULL, NULL, NULL, NULL, 2, 0, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('11050102', '日志列表', '110501', 3, 'sys:log:list', NULL, NULL, NULL, NULL, 3, 1, 0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('488299583537157', '字典管理', '11', 2, 'dict', '/system/dict', 'system/dict/index', NULL, 'ele-notebook', 3, 1,
0, 1);

INSERT INTO sys_menu(id, name, pid, type, id_code, route_url, component_path,
route_redirect, icon, seq, is_show, is_cache, enabled)
VALUES ('489978240356357', '系统配置', '11', 2, 'sys:config:manager', '/system/config', 'system/config/index', NULL,
'ele-cpu',
4, 1, 0, 1);