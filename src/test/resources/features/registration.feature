Feature: User Registration

  Background:
    * url baseUrl + '/auth' // Correct endpoint

  Scenario: Successful registration with valid data
    Given path '/register'
    And request { fullname: 'J Doe', username: 'jdoe', email: 'j@example.com', password: 'password123' }
    When method POST
    Then status 200
    And match response == 'User registered successfully'