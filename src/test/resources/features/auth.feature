Feature: Authentication - Login and Registration

  Background:
    * url 'http://localhost:8080/api/auth'

  Scenario: Successful registration with valid data
    Given path '/register'
    And request { fullname: 'John Doe', username: 'johndoe', email: 'john@example.com', password: 'password123' }
    When method POST
    Then status 200
    And match response == 'User registered successfully'

  Scenario: Failed registration with existing username
    Given path '/register'
    And request { fullname: 'Jane Doe', username: 'johndoe', email: 'jane@example.com', password: 'password123' }
    When method POST
    Then status 400
    And match response == 'Username already exists'

  Scenario: Successful login with valid credentials
    Given path '/login'
    And request { username: 'johndoe', password: 'password123' }
    When method POST
    Then status 200
    And match response == { message: 'Login successful', role: '#string', token: '#string', redirect: '#string' }

  Scenario: Failed login with invalid credentials
    Given path '/login'
    And request { username: 'wronguser', password: 'wrongpass' }
    When method POST
    Then status 401
    And match response == { error: 'Invalid username or password' }

  Scenario: Admin login with hardcoded credentials
    Given path '/login'
    And request { username: 'Abhainn', password: 'admin' }
    When method POST
    Then status 200
    And match response == { message: 'Login successful', role: 'ADMIN', token: '#string', redirect: '/admin' }