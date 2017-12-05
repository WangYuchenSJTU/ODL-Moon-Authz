curl -l -H "Content-type: application/json" -X POST -d '{"method":"authz","username":"bob"}' "http://localhost:8080/shiroservlet/ShiroServlet"
curl -l -H "Content-type: application/json" -X POST -d '{"method":"authc","username":"lucas","password":"123456"}' "http://localhost:8080/shiroservlet/ShiroServlet"

