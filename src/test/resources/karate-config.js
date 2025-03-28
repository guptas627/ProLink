function fn() {
  var config = {
    baseUrl: 'http://localhost:8080/api', // Base URL for your API
    token: '' // Initialize token as empty
  };

  // Register a new user (if not already registered)
  var registerResponse = karate.callSingle('classpath:features/registration.feature', config);
  if (registerResponse.status === 200) {
    console.log('User registered successfully');
  } else {
    console.error('User registration failed');
    return config; // Return if registration fails
  }

  // Log in the user and store the token
  var loginResponse = karate.callSingle('classpath:features/login.feature', config);
  if (loginResponse.status === 200 && loginResponse.token) {
    config.token = loginResponse.token; // Store the token in the config
    console.log('User logged in successfully. Token:', config.token);
  } else {
    console.error('Login failed or token not found');
  }

  return config; // Return the config object with the token
}
