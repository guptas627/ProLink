export default {
  template: `
  <div>
      <div class="nav-bar">
          <img src="/assets/images/logo.jpg" alt="ProLink Logo" class="logo">
          <div class="nav-links">
              <button class="nav-btn" @click="goToHome"><i class="fas fa-home"></i><span>Home</span></button>
              <button class="nav-btn" @click="goToNetwork"><i class="fas fa-users"></i><span> My Network</span></button>
              <button class="nav-btn" @click="goToJobs"><i class="fas fa-briefcase"></i><span> Jobs</span></button>
              <button class="nav-btn active" @click="goToMessaging"><i class="fas fa-comments"></i><span> Messaging</span></button>
              <div class="profile-dropdown">
                  <img :src="getProfilePictureUrl()" class="profile-pic-nav">
                  <i class="fas fa-caret-down dropdown-arrow"></i>
                  <div class="dropdown-content">
                      <button @click="logout"><i class="fas fa-sign-out-alt"></i> Logout</button>
                  </div>
              </div>
          </div>
      </div>

      <div class="messaging-dashboard">
          <div class="recent-conversations">
              <h2>Recent</h2>
              <ul>
                  <li v-for="connection in connections" :key="connection.username" @click="openChat(connection)">
                      <img :src="connection.profilePictureUrl || '/assets/images/admin-avatar.png'" class="profile-pic">
                      <div class="conversation-preview">
                          <strong>{{ connection.fullName }}</strong>
                          <p>{{ getLastMessagePreview(connection.username) }}</p>
                      </div>
                      <span class="timestamp">{{ getLastMessageTime(connection.username) }}</span>
                  </li>
              </ul>
          </div>

          <div class="chat-window" v-if="selectedUser">
              <div class="chat-header">
                  <img :src="selectedUser.profilePictureUrl || '/assets/images/admin-avatar.png'" class="profile-pic">
                  <strong>{{ selectedUser.fullName }}</strong>
              </div>

			  <div class="chat-messages">
			  <div v-for="message in messages" 
			       :key="message.id" 
			       :class="getContainerClass(message)">
			      
			      <div :class="getMessageClass(message)">
			          <p>{{ message.content }}</p>
			      </div>
			      
			      <span class="message-time">{{ formatTimestamp(message.timestamp) }}</span>
			  </div>


			  </div>


			  <div class="chat-input">
			      <input type="text" v-model="newMessage" placeholder="Type a message" @keyup.enter="sendMessage">
			      <button @click="sendMessage"><i class="fas fa-paper-plane"></i></button>
			  </div>
          </div>

          <div class="empty-chat" v-else>
              <p>Select a conversation to start messaging</p>
          </div>
      </div>
  </div>
  `,

  data() {
    return {
      connections: [],
      messages: [],
      selectedUser: null,
      newMessage: "",
      currentUser: null,
    };
  },

  methods: {
	getMessageClass(message) {
	    if (!this.currentUser || !this.currentUser.username) {
	        console.log("getMessageClass: No current user found. Assigning 'received'.");
	        return "received";
	    }

	    const senderUsername = typeof message.sender === "object" ? message.sender.username : message.sender;
	    const assignedClass = senderUsername === this.currentUser.username ? "sent" : "received";

	    console.log(`getMessageClass: Current User: '${this.currentUser.username}', Message from: '${senderUsername}', Assigned Class: '${assignedClass}'`);
	    return assignedClass;
	},

	getContainerClass(message) {
	    if (!this.currentUser || !this.currentUser.username) {
	        console.log("getContainerClass: No current user found. Assigning 'received-container'.");
	        return "received-container";
	    }

	    const senderUsername = typeof message.sender === "object" ? message.sender.username : message.sender;
	    const assignedContainer = senderUsername === this.currentUser.username ? "sent-container" : "received-container";

	    console.log(`getContainerClass: Current User: '${this.currentUser.username}', Message from: '${senderUsername}', Assigned Container: '${assignedContainer}'`);
	    return assignedContainer;
	},
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
	        if (!response.ok) throw new Error("Failed to fetch user profile");

	        this.currentUser = await response.json();
	        this.fetchConnections();
	    } catch (error) {
	        console.error("Error fetching user profile:", error);
	    }
	},

	async fetchConnections() {
	    try {
	        if (!this.currentUser) return;

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
	        if (!response.ok) throw new Error("Failed to fetch connections");

	        this.connections = await response.json();

	        // Sort conversations based on last message timestamp (assuming backend returns timestamps)
	        this.connections.sort((a, b) => new Date(b.lastMessageTime) - new Date(a.lastMessageTime));

	        // Auto-open the most recent conversation
	        if (this.connections.length > 0) {
	            this.selectedUser = this.connections[0];
	            this.fetchMessages();
	        }
	    } catch (error) {
	        console.error("Error fetching connections:", error);
	    }
	},

	async fetchMessages() {
	    if (!this.selectedUser) return;

	    try {
	        const token = localStorage.getItem("jwtToken");
	        if (!token) {
	            console.error("No JWT token found in localStorage.");
	            window.location.href = "/login";
	            return;
	        }

	        const response = await fetch(`/api/messages/${this.currentUser.username}/${this.selectedUser.username}`, {
	            headers: {
	                "Authorization": `Bearer ${token}`,
	            },
	        });
	        if (!response.ok) throw new Error("Failed to fetch messages");

	        this.messages = await response.json();
	    } catch (error) {
	        console.error("Error fetching messages:", error);
	    }
	},

	async sendMessage() {
	    if (!this.newMessage.trim() || !this.selectedUser) {
	        console.error("No message content or recipient selected");
	        return;
	    }

	    const token = localStorage.getItem("jwtToken");
	    if (!token) {
	        console.error("No JWT token found in localStorage.");
	        window.location.href = "/login";
	        return;
	    }

	    const messageData = {
	        sender: this.currentUser.username,
	        receiver: this.selectedUser.username,
	        content: this.newMessage.trim(),
	    };

	    try {
	        const response = await fetch("/api/messages/send", {
	            method: "POST",
	            headers: {
	                "Authorization": `Bearer ${token}`,
	                "Content-Type": "application/json",
	            },
	            body: JSON.stringify(messageData),
	        });

	        if (!response.ok) {
	            const errorData = await response.json();
	            throw new Error(`Failed to send message: ${errorData.message || "Unknown error"}`);
	        }

	        const newMessage = await response.json();
	        this.messages.push(newMessage);
	        this.newMessage = "";
	    } catch (error) {
	        console.error("Error sending message:", error);
	    }
	},

    openChat(user) {
      this.selectedUser = user;
      this.fetchMessages();
    },

	getLastMessagePreview(username) {
	    const userMessages = this.messages.filter(m => m.sender === username || m.receiver === username);
	    const lastMessage = userMessages.pop(); // Get last message

	    if (!lastMessage) {
	        return "Start conversation!";
	    }

	    const senderUsername = typeof lastMessage.sender === "object" ? lastMessage.sender.username : lastMessage.sender;
	    
	    if (senderUsername === this.currentUser.username) {
	        return `<strong>You:</strong> ${lastMessage.content}`;
	    } else {
	        return `<strong>${this.getUserFullName(username)}:</strong> ${lastMessage.content}`;
	    }
	},
	getUserFullName(username) {
	    const user = this.connections.find(conn => conn.username === username);
	    return user ? user.fullName : username;
	},


    getLastMessageTime(username) {
      const lastMessage = this.messages.filter(m => m.sender === username || m.receiver === username).pop();
      return lastMessage ? this.formatTimestamp(lastMessage.timestamp) : "";
    },

    formatTimestamp(timestamp) {
      return new Date(timestamp).toLocaleString();
    },

    getProfilePictureUrl() {
      return this.currentUser?.profilePictureUrl || "/assets/images/admin-avatar.png";
    },

    goToHome() { window.location.href = "/users"; },
	goToNetwork() { window.location.href = "/network"; },
    goToJobs() { window.location.href = "/jobs"; },
	logout() {
	    localStorage.removeItem("jwtToken");
	    localStorage.removeItem("username");
	    window.location.href = "/login";
	}
  },
  mounted() {
      this.fetchCurrentUser().then(() => {
          if (this.connections.length > 0) {
              // Select the most recent connection
              this.selectedUser = this.connections[0];
              this.fetchMessages();
          }
      });

      // Fetch messages every 3 seconds
      setInterval(() => {
          if (this.selectedUser) {
              this.fetchMessages();
          }
      }, 3000);
  }

};

  const userStyles = document.createElement("style");
  userStyles.innerHTML = `
  /* ✅ Main Layout */
  .user-dashboard {
      font-family: Arial, sans-serif;
      background-color: #f3f2ef;
      min-height: 100vh;
      padding: 20px;
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
  .conversation-preview {
      flex-grow: 1;
  }

  .conversation-preview strong {
      font-size: 14px;
      color: #333;
  }

  .conversation-preview p {
      font-size: 12px;
      color: #666;
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
	width: 40px;
	height: 40px;
	border-radius: 50%;
    margin-right: 12px;

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
  .messaging-dashboard {
      display: flex;
      height: 80vh;
  }

  .recent-conversations {
      width: 30%;
      background: #f3f2ef;
      height: 100vh;
      overflow-y: auto;
      border-right: 1px solid #ddd;
      padding: 15px;
  }
  .recent-conversations h2 {
      color: #007bff;
      font-size: 25px;
      font-weight: bold;
  }

  .recent-conversations ul {
      list-style: none;
      padding: 0;
      margin: 0;
  }

  .recent-conversations li {
      display: flex;
      align-items: center;
      padding: 12px;
      border-bottom: 1px solid #ddd;
      cursor: pointer;
      transition: background 0.3s;
  }

  .recent-conversations li:hover, 
  .recent-conversations li.active {
      background: #e9e9e9; /* Highlight active chat */
  }


  .chat-window {
      width: 70%;
      display: flex;
      flex-direction: column;
      height: 90vh;
      background: #fff;
  }
  .chat-header {
      display: flex;
      align-items: center;
      padding: 35px;
      background: #f8f9fa;
      border-bottom: 1px solid #ddd;
	  gap: 15px;
  }
  .chat-header strong {
      font-size: 18px;
      font-weight: bold;
      margin-top: 8px; /* Moves name a little down */
  }

  .chat-input {
      display: flex;
      align-items: center;
      background-color: #ffffff;
      border-radius: 25px;
      padding: 8px 15px;
      box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
      border: 1px solid #ddd;
      width: 95%;
      margin: 10px auto;
  }

  .chat-input input {
      flex: 1;
      border: none;
      outline: none;
      padding: 10px;
      font-size: 14px;
      border-radius: 25px; /* Match container */
      background-color: transparent;
  }

  .chat-input button {
      background-color: #007bff; /* Blue color */
      border: none;
      border-radius: 50%;
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: background 0.3s;
  }

  .chat-input button i {
      color: white;
      font-size: 16px;
  }

  .chat-input button:hover {
      background-color: #0056b3; /* Darker blue on hover */
  }


  .chat-messages {
      flex-grow: 1;
      overflow-y: auto;
      padding: 15px;
      display: flex;
      flex-direction: column;
  }
  
  .sent-container,
  .received-container {
      display: flex !important;
      flex-direction: column !important;
      margin-bottom: 10px !important;
      width: 100% !important;
  }

  .sent-container {
      align-items: flex-end !important; /* Align sent messages to the right */
  }

  .received-container {
      align-items: flex-start !important; /* Align received messages to the left */
  }

  .sent {
      background-color: #0073b1 !important; /* Blue for sent messages */
      color: white !important;
      padding: 10px 15px;
      border-radius: 15px;
      max-width: 60%;
      word-wrap: break-word;
      text-align: right;
  }

  /* ✅ Received Messages (Other User) */
  .received {
      background-color: #34c759 !important; /* Green for received messages */
      color: white !important;
      padding: 10px 15px;
      border-radius: 15px;
      max-width: 60%;
      word-wrap: break-word;
      text-align: left;
  }

  .message-time {
      font-size: 12px;
      color: #666;
      margin-top: 3px;
      display: block;
      text-align: center; /* Centered below message */
  }

  

  `;

  document.head.appendChild(userStyles);

	
	
