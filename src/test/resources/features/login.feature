Feature: User Login

  Background:
    * url baseUrl + '/auth'

  Scenario: Successful login with valid credentials
    Given path '/login'
    And request { username: 'jdoe', password: 'password123' }
    When method POST
    Then status 200
    And match response == { message: 'Login successful', role: '#string', token: '#string', redirect: '#string' }
    And def token = response.token