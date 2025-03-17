export default {
  template: `
  <div class="login-container">
      <div class="top-bar">
          <img src="/assets/images/logo.jpg" alt="ProLink Logo" class="logo">
      </div>

      <div class="login-box">
          <h2 class="login-title">Sign in</h2>
          <p class="login-subtitle">Stay updated on your professional world.</p>

          <form @submit.prevent="handleLogin" class="login-form">
              <div class="form-group">
                  <label for="username">Username</label>
                  <input type="text" id="username" class="form-control" v-model="username" required placeholder="Enter your username">
              </div>

              <div class="form-group">
                  <label for="password">Password</label>
                  <input type="password" id="password" class="form-control" v-model="password" required placeholder="Enter your password">
              </div>

              <button type="submit" class="btn-primary">Sign in</button>

              <div v-if="errorMessage" class="alert">
                  {{ errorMessage }}
              </div>
          </form>

          <div class="signup-section">
              <span>New to ProLink?</span> 
              <a href="./register" class="signup-link"> Join now</a>
          </div>
      </div>
  </div>
  `,
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    };
  },
  methods: {
	async handleLogin() {
	  this.errorMessage = '';

	  try {
	    const response = await fetch('/api/auth/login', {
	      method: 'POST',
	      headers: { 'Content-Type': 'application/json' },
	      body: JSON.stringify({ username: this.username, password: this.password })
	    });

	    if (!response.ok) {
	      throw new Error('Invalid username or password');
	    }

	    const data = await response.json();
		console.log(data);

	    // Store token and role in localStorage
	    localStorage.setItem('jwtToken', data.token); // Store JWT token
	    localStorage.setItem('role', data.role); // Store user role
		localStorage.setItem('username',this.username);
		console.log(data.role);

	    // Redirect based on username or role
	    if (this.username === 'abhainn') {
	      window.location.href = '/admin'; // Redirect to admin dashboard for this specific username
	    } else if (data.role === 'ADMIN') {
	      window.location.href = '/admin'; // Redirect to admin dashboard for other admins
	    } else {
	      window.location.href = '/users'; // Redirect to user dashboard for regular users
	    }

	  } catch (error) {
	    this.errorMessage = 'Error: ' + error.message;
	  }
	}
  }
};