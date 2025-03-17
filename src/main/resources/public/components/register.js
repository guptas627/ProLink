export default {
  template: `
  <div class="register-container">
      <div class="top-bar">
          <img src="/assets/images/logo.jpg" alt="ProLink Logo" class="logo">
      </div>

      <div class="register-box">
          <h2 class="register-title">Join ProLink</h2>
          <p class="register-subtitle">Connect with professionals worldwide.</p>

          <form @submit.prevent="handleRegister" class="register-form">
              <div class="form-group">
                  <label for="fullname">Full Name</label>
                  <input type="text" id="fullname" class="form-control" v-model="fullname" required placeholder="Enter your full name">
              </div>

              <div class="form-group">
                  <label for="username">Username</label>
                  <input type="text" id="username" class="form-control" v-model="username" required placeholder="Choose a username">
              </div>

              <div class="form-group">
                  <label for="email">Email</label>
                  <input type="email" id="email" class="form-control" v-model="email" required placeholder="Enter your email">
              </div>

              <div class="form-group">
                  <label for="password">Password</label>
                  <input type="password" id="password" class="form-control" v-model="password" required placeholder="Create a password">
              </div>

              <div class="form-group">
                  <label for="confirmPassword">Confirm Password</label>
                  <input type="password" id="confirmPassword" class="form-control" v-model="confirmPassword" required placeholder="Confirm your password">
              </div>

              <button type="submit" class="btn-primary">Sign Up</button>

              <div v-if="errorMessage" class="alert">
                  {{ errorMessage }}
              </div>
          </form>

          <div class="login-section">
              <span>Already on ProLink?</span> 
              <a href="/login" class="login-link">Sign in</a>
          </div>
      </div>
  </div>
  `,
  data() {
    return {
      fullname: '',
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      errorMessage: ''
    };
  },
  methods: {
    async handleRegister() {
      this.errorMessage = '';

      if (this.password !== this.confirmPassword) {
        this.errorMessage = "Passwords do not match!";
        return;
      }

      try {
        const response = await fetch('/api/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            fullname: this.fullname,
            username: this.username,
            email: this.email,
            password: this.password
          })
        });

        if (!response.ok) {
          throw new Error('Registration failed! Try again.');
        }

        window.location.href = "/login"; // Redirect to login page after successful registration

      } catch (error) {
        this.errorMessage = 'Error: ' + error.message;
      }
    }
  }
};