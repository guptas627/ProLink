export default {
  template: `
  <div>
      <!-- Toast Notification -->
	  <div v-show="toastMessage" class="toast" :class="toastType">
	      {{ toastMessage }}
	  </div>
      <!-- Navigation Bar -->
      <div class="nav-bar">
          <img src="/assets/images/logo.jpg" alt="ProLink Logo" class="logo">
          <div class="nav-links">
              <button class="nav-btn" @click="goToHome"><i class="fas fa-home"></i><span>Home</span></button>
              <button class="nav-btn active" @click="goToNetwork"><i class="fas fa-users"></i><span> My Network</span></button>
              <button class="nav-btn" @click="goToJobs"><i class="fas fa-briefcase"></i><span> Jobs</span></button>
              <button class="nav-btn" @click="goToMessaging"><i class="fas fa-comments"></i><span> Messaging</span></button>
              <div class="profile-dropdown">
                  <img :src="getProfilePictureUrl()" class="profile-pic-nav">
                  <i class="fas fa-caret-down dropdown-arrow"></i>
                  <div class="dropdown-content">
                      <button @click="logout"><i class="fas fa-sign-out-alt"></i> Logout</button>
                  </div>
              </div>
          </div>
      </div>

      <!-- Network Page Content -->
	  <div class="network-dashboard">
      <div class="network-content">
          <h1>My Network</h1>
          <div class="search-bar">
              <input type="text" v-model="searchQuery" placeholder="Search users..." @input="searchUsers">
          </div>
          <div v-if="searchResults.length > 0" class="search-results">
              <h2>Search Results</h2>
              <ul>
                  <li v-for="user in searchResults" :key="user.username">
                      <img :src="user.profilePictureUrl || '/assets/images/admin-avatar.png'" class="profile-pic">
                      <span>{{ user.fullName }}</span>
                      <button v-if="!isConnected(user.username)" @click="sendConnectionRequest(user.username)">Connect</button>
                  </li>
              </ul>
          </div>
          <div v-if="pendingRequests.length > 0" class="pending-requests">
              <h2>Pending Requests</h2>
			  <ul>
			      <li v-for="request in pendingRequests" :key="request.username">
			          <img :src="request.profilePictureUrl || '/assets/images/admin-avatar.png'" class="profile-pic">
			          <span>{{ request.fullName }}</span>
			          <button v-if="currentUser && request.username !== currentUser.username" 
			                  @click="acceptRequest(request.username)">Accept</button>
			          <button v-if="currentUser && request.username !== currentUser.username"
			                  @click="rejectRequest(request.username)">Reject</button>
			      </li>
			  </ul>
          </div>
          <div v-if="connections.length > 0" class="connections">
              <h2>Your Connections</h2>
              <ul>
                  <li v-for="connection in connections" :key="connection.username">
                      <img :src="connection.profilePictureUrl || '/assets/images/admin-avatar.png'" class="profile-pic">
                      <span>{{ connection.fullName }}</span>
                  </li>
              </ul>
          </div>
      </div>
	  </div>
  </div>
  `,

  data() {
    return {
      searchQuery: "",
      searchResults: [],
      pendingRequests: [],
      connections: [],
      currentUser: null, // Store full user object (username, profile picture, etc.)
	  toastMessage: "",  // ✅ Holds toast message
	  toastType: "",
    };
  },

  methods: {
    async fetchCurrentUser() {
      try {
        const storedUsername = localStorage.getItem("username");
        if (!storedUsername) {
          console.error("No username found in localStorage.");
          window.location.href = "/login";
          return;
        }

        const token = localStorage.getItem("jwtToken");
        if (!token) {
          console.error("No JWT token found in localStorage.");
          window.location.href = "/login";
          return;
        }

        const response = await fetch(`/api/users/profile/${storedUsername}`, {
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error(`Error fetching user profile: ${response.status}`);
        }

        this.currentUser = await response.json();
        console.log("Fetched User Profile:", this.currentUser);
      } catch (error) {
        console.error("Error fetching user profile:", error);
      }
    },

    showToast(message, type = "success") {
	    this.toastMessage = message;
	    this.toastType = type;

	    this.$nextTick(() => {
	        const toastElement = document.querySelector(".toast");
	        if (toastElement) {
	            toastElement.classList.add("show"); // Show toast
	        }
	    });

	    setTimeout(() => {
	        this.toastMessage = "";
	        this.$nextTick(() => {
	            const toastElement = document.querySelector(".toast");
	            if (toastElement) {
	                toastElement.classList.remove("show"); // Hide toast after timeout
	            }
	        });
	    }, 3000);
	},

    async fetchPendingRequests() {
      try {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
          console.error("No JWT token found in localStorage.");
          window.location.href = "/login";
          return;
        }

        const response = await fetch(`/api/users/profile/${this.currentUser.username}/pending-requests`, {
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error(`Error: ${response.status}`);
        }
        this.pendingRequests = await response.json();
      } catch (error) {
        console.error("Error fetching pending requests:", error);
      }
    },

    async fetchConnections() {
      try {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
          console.error("No JWT token found in localStorage.");
          window.location.href = "/login";
          return;
        }

        const response = await fetch(`/api/users/profile/${this.currentUser.username}/connections`, {
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error(`Error: ${response.status}`);
        }
        this.connections = await response.json();
      } catch (error) {
        console.error("Error fetching connections:", error);
      }
    },

    async sendConnectionRequest(username) {
      try {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
          console.error("No JWT token found in localStorage.");
          window.location.href = "/login";
          return;
        }

        const response = await fetch(`/api/users/profile/${this.currentUser.username}/connect/${username}`, {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error("Failed to send request");
        }
		this.showToast("✅ Connection request sent!", "success");
		this.searchUsers();
      } catch (error) {
        console.error("Error sending request:", error);
		this.showToast("❌ Failed to send request.", "error");
      }
    },

    async searchUsers() {
	    if (!this.searchQuery.trim()) {
	        this.searchResults = [];
	        return;
	    }
	    try {
	        const token = localStorage.getItem("jwtToken");
	        if (!token) {
	            console.error("No JWT token found in localStorage.");
	            window.location.href = "/login";
	            return;
	        }

	        const response = await fetch(`/api/users/search?query=${encodeURIComponent(this.searchQuery)}&username=${this.currentUser.username}`, {
	            headers: {
	                "Authorization": `Bearer ${token}`,
	            },
	        });
	        if (!response.ok) {
	            throw new Error(`Error: ${response.status}`);
	        }
	        const data = await response.json();
	        console.log("Search Results Data:", data); // ✅ Debug API response

	        this.searchResults = Array.isArray(data) ? data : [];
	    } catch (error) {
	        console.error("Error searching users:", error);
	        this.searchResults = [];
	    }
	},

    async acceptRequest(username) {
      try {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
          console.error("No JWT token found in localStorage.");
          window.location.href = "/login";
          return;
        }

        const response = await fetch(`/api/users/profile/${this.currentUser.username}/accept/${username}`, {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error("Failed to accept request");
        }
        this.showToast("✅ Connection accepted!", "success");
        this.fetchPendingRequests();
        this.fetchConnections();
      } catch (error) {
        console.error("Error accepting request:", error);
		this.showToast("❌ Failed to accept request.", "error");
      }
    },

    async rejectRequest(username) {
	    if (!this.currentUser?.username) return;

	    try {
	        const token = localStorage.getItem("jwtToken");
	        if (!token) {
	            console.error("No JWT token found in localStorage.");
	            window.location.href = "/login";
	            return;
	        }

	        const response = await fetch(`/api/users/profile/${this.currentUser.username}/reject/${username}`, {
	            method: "POST",
	            headers: {
	                "Authorization": `Bearer ${token}`,
	            },
	        });

	        if (!response.ok) {
	            throw new Error("Failed to reject request");
	        }

			this.showToast("❌ Connection request rejected.", "error");
	        this.pendingRequests = this.pendingRequests.filter(request => request.username !== username);
	        
	    } catch (error) {
	        console.error("Error rejecting request:", error);
			this.showToast("❌ Failed to reject request.", "error");
	    }
	},

    isConnected(username) {
      return this.connections.some(connection => connection.username === username);
    },

    getProfilePictureUrl() {
      if (this.currentUser?.profilePictureUrl) {
        return this.currentUser.profilePictureUrl.startsWith("http")
          ? this.currentUser.profilePictureUrl
          : `http://localhost:8080${this.currentUser.profilePictureUrl}`;
      }
      return "/assets/images/admin-avatar.png";
    },

    goToHome() {
      window.location.href = "/users";
    },

    goToJobs() {
      window.location.href = "/jobs";
    },

    goToMessaging() {
      window.location.href = "/messaging";
    },

    logout() {
      localStorage.removeItem("jwtToken");
      localStorage.removeItem("username");
      window.location.href = "/login";
    }
  },

  async mounted() {
    try {
      await this.fetchCurrentUser();
      await this.fetchPendingRequests();
      await this.fetchConnections();
    } catch (error) {
      console.error("Error initializing user data:", error);
    }
  }
};



  const userStyles = document.createElement("style");
  userStyles.innerHTML = `
  /* ✅ Main Layout */
  .network-dashboard {
      display: flex;
      justify-content: center; /* Centers the box */
      align-items: center; /* Centers it vertically */
      background-color: #f3f2ef !important; 
      width: 100%;
      min-height: 100vh; /* Full height */
      padding-top: 80px; /* Avoids overlap with navbar */
  }





  /* ✅ Navigation Bar */
  .nav-bar {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: auto;
      background-color: white;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 50px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      z-index: 1000;
  }

  /* ✅ Logo */
  .logo {
      height: 40px;
      width: auto;
  }

  /* ✅ Navbar Buttons */
  .nav-links {
      display: flex;
      align-items: center;
      gap: 20px;
      justify-content: center;
      width: 45%;
  }

  .nav-btn {
      background: none;
      border: none;
      padding: 8px 10px;
      flex-direction: column;
      font-size: 14px;
      cursor: pointer;
      font-weight: bold;
      color: #666;
      display: flex;
      align-items: center;
      gap: 2px;
      width: 70px;
      transition: 0.3s;
  }

  .nav-btn i {
      font-size: 20px;
      color: #666;
  }

  .nav-btn span {
      font-size: 12px;
      white-space: nowrap;
  }

  .nav-btn.active {
      color: black;
      font-weight: bold;
  }

  .nav-btn.active i {
      color: black;
  }

  /* ✅ User Profile Dropdown */
  .profile-dropdown {
      position: relative;
      display: flex;
      align-items: center;
      cursor: pointer;
      gap: 5px;
      padding-bottom: 10px;
  }

  .profile-pic-nav {
      width: 35px;
      height: 35px;
      border-radius: 50%;
      border: 2px solid #0073b1;
  }

  .dropdown-arrow {
      font-size: 14px;
      color: #666;
  }

  .dropdown-content {
      display: none;
      position: absolute;
      top: 45px;
      right: 0;
      background: white;
      border-radius: 5px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
      min-width: 150px;
      text-align: left;
      padding: 5px 0;
  }

  .profile-dropdown:hover .dropdown-content {
      display: block;
  }

  .dropdown-content button {
      background: none;
      border: none;
      width: 100%;
      padding: 10px;
      cursor: pointer;
      font-size: 14px;
      color: #333;
      text-align: left;
      display: flex;
      align-items: center;
      gap: 10px;
      padding-left: 15px;
  }

  .dropdown-content button i {
      font-size: 16px;
      color: #666;
  }

  .dropdown-content button:hover i {
      color: black;
  }

  .dropdown-content button:hover {
      background: #f3f3f3;
  }
  /* ✅ My Network Page */

 
  .network-content {
      width: 750px; /* Fixed width */
      background: white;
      border-radius: 10px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      padding: 20px;
      text-align: center;
      min-height: auto; /* Prevents unnecessary elongation */
	  margin-top: -500px;
  }

  /* ✅ Search Bar */
  .search-bar {
      margin-bottom: 20px;
      display: flex;
      justify-content: center;
  }

  .search-bar input {
      width: 100%;
      padding: 10px;
      border: 1px solid #ccc;
      border-radius: 5px;
      font-size: 16px;
  }

  /* ✅ User Lists */
  .search-results, .pending-requests, .connections {
      margin-top: 20px;
  }

  .search-results h2, .pending-requests h2, .connections h2 {
      font-size: 20px;
      margin-bottom: 10px;
      color: #0073b1;
  }

  ul {
      list-style: none;
      padding: 0;
  }

  ul li {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: #f9f9f9;
      padding: 10px;
      border-radius: 5px;
      margin-bottom: 10px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }

  ul li img.profile-pic {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      border: 2px solid #0073b1;
      margin-right: 10px;
  }

  ul li span {
      font-size: 16px;
      font-weight: bold;
      flex: 1;
  }

  ul li button {
      padding: 8px 12px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-weight: bold;
  }

  ul li button:hover {
      opacity: 0.8;
  }

  /* ✅ Buttons */
  button {
      cursor: pointer;
  }

  button.connect {
      background: #0073b1;
      color: white;
  }

  button.accept {
      background: #28a745;
      color: white;
  }

  button.reject {
      background: #dc3545;
      color: white;
  }
  /* ✅ Toast Notification */
  .toast {
      position: fixed;
      bottom: 50px;
      right: 20px;
      background: #444;
      color: white;
      padding: 12px 20px;
      border-radius: 10px;
      box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
      font-size: 14px;
      z-index: 9999;
      opacity: 0; /* Initially hidden */
      visibility: hidden; /* Hide when there's no message */
      transition: opacity 0.5s ease-in-out, visibility 0.5s ease-in-out;
  }

  .toast.success {
      background: #90EE90;
  }

  .toast.error {
      background: #FFA07A;
  }

  .toast.show {
      opacity: 1;
      visibility: visible;
  }

  @keyframes fadein {
      from { opacity: 0; transform: translateY(10px); }
      to { opacity: 1; transform: translateY(0); }
  }

  @keyframes fadeout {
      from { opacity: 1; transform: translateY(0); }
      to { opacity: 0; transform: translateY(10px); }
  }


  `;

  document.head.appendChild(userStyles);

