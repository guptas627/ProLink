export default {
  template: `
  <div class="admin-dashboard">
      <!-- ✅ Navigation Bar -->
      <div class="nav-bar">
          <img src="/assets/images/logo.jpg" alt="ProLink Logo" class="logo">
		  <div class="nav-buttons">
		  <button @click="openChartsModal" class="btn-charts">
		      <span class="glyphicon glyphicon-stats"></span> Skill's Chart
		  </button>
		  <button @click="logout" class="btn-logout">
		      <span class="glyphicon glyphicon-log-out"></span> Logout
		  </button>

		  </div>

      </div>
	  <!-- ✅ Normal Modal for Skills Chart -->
	  <div v-if="showChartsModal" class="modal-overlay modal-overlay-chart">
	      <div class="modal">
	          <div class="modal-header">
			  <button class="close-btn" @click="closeChartsModal">
			      <span class="glyphicon glyphicon-remove"></span>
			  </button>

	              <h3 class="modal-title">User's Skills Chart</h3>
	          </div>
	          <div class="modal-body">
			     <canvas id="skillsChart"></canvas>
	          </div>
	          <div class="modal-footer">
	              <button @click="closeChartsModal" class="btn-close-modal">Close</button>
	          </div>
	      </div>
	  </div>


      <!-- ✅ Users Section -->
      <h2 class="users-heading">Users</h2>
      <hr class="divider">

      <!-- ✅ Extract Users Section -->
      <div class="export-options">
          <p>Extract Selected Users with:</p>
		  <button class="btn-json" @click="downloadJSON">
		      <span class="glyphicon glyphicon-download"></span> Download JSON
		  </button>
		  <button class="btn-xml" @click="downloadXML">
		      <span class="glyphicon glyphicon-save-file"></span> Download XML
		  </button>

      </div>

      <!-- ✅ Users Grid -->
      <div class="user-grid">
          <div v-for="user in users" :key="user.id" class="user-card">
              <img 
                :src="user.profilePictureUrl || '/assets/images/admin-avatar.png'" 
                class="profile-pic">
              <h3>{{ user.fullName }}</h3>
              <p>{{ user.bio || "No bio available" }}</p>
              <hr class="card-divider">
			  <button class="btn-profile" @click="viewProfile(user)">
			      <span class="glyphicon glyphicon-user"></span> Show Profile
			  </button>

			  <!-- ✅ Select Checkbox -->
			   <div class="select-container">
			         <input type="checkbox" :id="'select-' + user.id" v-model="selectedUsers" :value="user">
					 <label :for="'select-' + user.id">
					     <span class="glyphicon glyphicon-check"></span> Select
					 </label>

			   </div>
          </div>
      </div>

	  <!-- ✅ Fix the Modal Overlay and Ensure Bootstrap Doesn't Hide It -->
	  <div v-if="showModal" class="modal-overlay modal-overlay-profile">
	      <div class="modal">
	          <div class="modal-header">
	              <button class="close-btn" @click="closeModal">&times;</button>
	              <h3 class="modal-title">{{ selectedUser.fullName }}</h3>
	          </div>
	          <div class="modal-body">
	              <img 
	                  :src="selectedUser.profilePictureUrl || '/assets/images/admin-avatar.png'" 
	                  class="modal-profile-pic">
	              <p><strong>User Name:</strong> {{ selectedUser.username}}</p>
	              <p><strong>Email:</strong> {{ selectedUser.email}}</p>
	              <p><strong>Bio:</strong> {{ selectedUser.bio || "No bio available" }}</p>
	              <p><strong>Location:</strong> {{ selectedUser.location || "Not specified" }}</p>
	              <p><strong>Current Position:</strong> {{ selectedUser.currentPosition || "Not specified" }}</p>
	              <p><strong>Education:</strong> {{ selectedUser.education || "Not specified" }}</p>
	              <p><strong>Work Experience:</strong> {{ selectedUser.workExperience || "Not specified" }}</p>
	              <p><strong>Skills:</strong> {{ selectedUser.skills ? selectedUser.skills.join(', ') : "Not specified" }}</p>
	          </div>
	          <div class="modal-footer">
	              <button @click="closeModal" class="btn-close-modal">Close</button>
	          </div>
	      </div>
	  </div>


  </div>
  `,

  data() {
      return {
          users: [],
          showModal: false,
		  showChartsModal: false,
          selectedUser: {},
		  selectedUsers: []
      };
  },

  methods: {
	async fetchUsers() {
	      try {
	        const token = localStorage.getItem('jwtToken'); // Get token from localStorage
	        const response = await fetch('/api/admin/users', {
	          headers: {
	            'Authorization': `Bearer ${token}`,
	            'Content-Type': 'application/json'
	          }
	        });

	        if (!response.ok) {
	          throw new Error('Failed to fetch users');
	        }

	        this.users = await response.json();
	      } catch (error) {
	        console.error('Error fetching users:', error);
	      }
	    },
	  openChartsModal() {
	      this.showChartsModal = true;
	      this.$nextTick(() => {
	          const modalOverlay = document.querySelector(".modal-overlay-chart");
	          const modal = document.querySelector(".modal");

	          if (modalOverlay && modal) {
	              modalOverlay.classList.add("active");
	              modal.classList.add("active");
	          }
	          this.renderSkillsChart(); // ✅ Render the chart when modal opens
	      });
	  },
	  async renderSkillsChart() {
	      if (this.users.length === 0) {
	          await this.fetchUsers();
	      }

	      const skillOptions = [
	          "JavaScript", "Python", "React", "Vue.js", "Django", 
	          "SQL", "AI", "Cloud Computing", "Cybersecurity", "C++", "C"
	      ];

	      const skillCounts = skillOptions.reduce((acc, skill) => {
	          acc[skill] = 0;
	          return acc;
	      }, {});

	      this.users.forEach(user => {
	          if (user.skills) {
	              user.skills.forEach(skill => {
	                  if (skillCounts.hasOwnProperty(skill)) {
	                      skillCounts[skill] += 1;
	                  }
	              });
	          }
	      });

	      const labels = skillOptions; // ✅ Ensure all skills are always displayed
	      const data = labels.map(skill => skillCounts[skill]); // ✅ Preserve order

	      const ctx = document.getElementById("skillsChart").getContext("2d");

	      // ✅ Destroy previous instance to prevent duplicates
	      if (this.chartInstance) {
	          this.chartInstance.destroy();
	      }

	      // ✅ Create new chart with proper X-axis handling
	      this.chartInstance = new Chart(ctx, {
	          type: "bar",
	          data: {
	              labels: labels,
	              datasets: [{
	                  label: "Users per Skill",
	                  data: data,
	                  backgroundColor: [
	                      "rgba(255, 99, 132, 0.7)",  "rgba(54, 162, 235, 0.7)",
	                      "rgba(255, 206, 86, 0.7)",  "rgba(75, 192, 192, 0.7)",
	                      "rgba(153, 102, 255, 0.7)", "rgba(255, 159, 64, 0.7)",
	                      "rgba(231, 76, 60, 0.7)",   "rgba(46, 204, 113, 0.7)",
	                      "rgba(52, 152, 219, 0.7)",  "rgba(155, 89, 182, 0.7)",
	                      "rgba(241, 196, 15, 0.7)"
	                  ],
	                  borderColor: "rgba(44, 62, 80, 1)",
	                  borderWidth: 1
	              }]
	          },
	          options: {
	              responsive: true,
	              maintainAspectRatio: false,
	              scales: {
	                  x: {
	                      ticks: {
	                          font: {
	                              size: 14
	                          }
	                      },
	                      grid: {
	                          display: false
	                      },
	                      categoryPercentage: 0.8, // ✅ Ensures bars take up space properly
	                      barPercentage: 0.9, // ✅ Prevents bars from being too thin
	                  },
	                  y: {
	                      beginAtZero: true
	                  }
	              },
	              plugins: {
	                  legend: {
	                      position: "top",
	                      labels: {
	                          font: {
	                              size: 14
	                          }
	                      }
	                  }
	              }
	          }
	      });
	  },

	  closeChartsModal() {
	      $(".modal-overlay-chart").fadeOut(300);
	      this.showChartsModal = false;
	  },
	  

	  viewProfile(user) {
	          this.selectedUser = user;
	          this.showModal = true;

	          // ✅ Force Vue to make modal visible
	          this.$nextTick(() => {
	              document.querySelector(".modal-overlay").style.display = "flex";
	              document.querySelector(".modal").style.display = "block";
	          });
	      },
	  closeModal() {
		          this.showModal = false;
		          
		          // ✅ Hide modal properly
		          setTimeout(() => {
		              document.querySelector(".modal-overlay").style.display = "none";
		              document.querySelector(".modal").style.display = "none";
		          }, 300);
		},
		logout() {
		      localStorage.removeItem('jwtToken'); // Remove token on logout
		      localStorage.removeItem('role'); // Remove role on logout
		      window.location.href = '/login'; // Redirect to login page
		    },

		


	  downloadJSON() {
	          if (this.selectedUsers.length === 0) {
	              alert("Please select at least one user.");
	              return;
	          }

	          const jsonData = this.selectedUsers.map(({ password, ...user }) => user);
	          const blob = new Blob([JSON.stringify(jsonData, null, 2)], { type: "application/json" });
	          const link = document.createElement("a");
	          link.href = URL.createObjectURL(blob);
	          link.download = "selected_users.json";
	          link.click();
	      },

		  downloadXML() {
		          if (this.selectedUsers.length === 0) {
		              alert("Please select at least one user.");
		              return;
		          }

		          let xml = `<?xml version="1.0" encoding="UTF-8"?>\n<users>\n`;
		          this.selectedUsers.forEach(({ password, ...user }) => {
		              xml += `<user>\n`;
		              for (const key in user) {
		                  xml += `<${key}>${user[key]}</${key}>\n`;
		              }
		              xml += `</user>\n`;
		          });
		          xml += `</users>`;

		          const blob = new Blob([xml], { type: "application/xml" });
		          const link = document.createElement("a");
		          link.href = URL.createObjectURL(blob);
		          link.download = "selected_users.xml";
		          link.click();
		      }
  },
  
  

  mounted() {
      this.fetchUsers();
	  $(".btn-profile").on("click", function () {
	          alert("Profile button clicked!");
	      });
  }
};

// ✅ Scoped CSS for Admin Dashboard
const adminStyles = document.createElement("style");
adminStyles.innerHTML = `
/* ✅ Main Layout */
.admin-dashboard {
    text-align: center;
    font-family: Arial, sans-serif;
    background-color: #f3f2ef;
    min-height: 100vh;
    padding: 20px;
}

/* ✅ Navigation */

.nav-bar {
    width: 100%;
    background-color: white;
    display: flex;
    align-items: center;
    justify-content: space-between; /* Ensure logo left, button right */
    padding: 10px 50px;
    position: relative; /* Change from absolute */
    top: 0;
    height: auto;
}
/* ✅ Align Navigation Buttons */
.nav-buttons {
    display: flex;
    gap: 15px; /* Adds space between buttons */
    align-items: center;
}
/* ✅ Charts Button */
.btn-charts {
    background-color: #28a745; /* Green */
    color: white;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 8px;
    border-radius: 5px;
    font-weight: bold;
}
.modal-body canvas {
    max-width: 100%;
    height: 300px;
    margin: 0 auto;
}


/* ✅ Icons in Buttons */
.btn-charts i,
.btn-logout i {
    font-size: 18px;
}

/* ✅ Hover Effects */
.btn-charts:hover {
    background-color: #218838;
}

.btn-logout:hover {
    background-color: #005a8d;
}




/* ✅ Ensure Logo Scales Correctly */
.logo {
  width: auto; /* Maintain aspect ratio */
  height: 50px !important; /* Force size change */
  max-height: 200px !important; /* Set a limit */
  min-height: 50px; /* Ensure it doesn't shrink too much */
  margin-left: 30px; /* Adjust positioning */
  display: block; /* Prevent inline-block issues */
}


.btn-logout {
    background-color: #0073b1;
    color: white;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 8px;
    border-radius: 5px;
    font-weight: bold;
}





/* ✅ Users Section */
.users-heading {
    margin-top: 40px;
    font-size: 40px;
    font-weight: bold;
}

.divider {
    width: 100%;
    border: 1px solid #ddd;
    margin: 10px 0;
}

/* ✅ Export Options */
.export-options {
    margin: 10px;
    display: flex;
    justify-content: center; /* Center align buttons */
    gap: 10px; /* Adds space between buttons */
}
.export-buttons {
    display: flex; /* Ensure buttons stay inline */
    justify-content: center; /* Center the buttons */
    align-items: center; /* Align properly */
    gap: 15px; /* Adds space between buttons */
}


.btn-json, .btn-xml {
    padding: 10px 20px; /* Adjust padding */
    border: none;
    cursor: pointer;
    font-size: 14px;
    font-weight: bold;
    border-radius: 5px; /* Rounded edges */

}

.btn-json {
    background-color: #0073b1;
    color: white;
}

.btn-xml {
    background-color: #28a745;
    color: white;
}

/* ✅ User Grid */
.user-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 50px;
    justify-content: flex-start; /* Align from the left */
    margin-top: 20px;
    width: 80vw; /* Ensure it fits well */
    max-width: 1200px; /* Prevent excessive stretching */
    padding: 0 10px;
}

/* ✅ User Card */
.user-card {
    background: white;
    padding: 15px;
    text-align: center;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    width: calc(33.33% - 20px); /* Fit exactly 3 in a row */
    min-width: 250px; /* Ensure minimum width */
    max-width: 320px; /* Prevent excessive stretching */
    flex-grow: 1;
}




.profile-pic {
    width: 80px;
    height: 80px;
    border-radius: 50%;
}

.card-divider {
    border: 1px solid #ddd;
    margin: 10px 0;
}

.btn-profile {
    background-color: #0073b1;
    color: white;
    border: none;
    padding: 8px;
    cursor: pointer;
}

/* ✅ Modal */
.modal-overlay {
    display: none;
    align-items: center;
    justify-content: center;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.6); /* Ensure proper overlay */
    z-index: 1050;
}
.modal-overlay-profile.active {
    display: flex !important;
}

/* ✅ Make Skills Chart Modal Independent */
.modal-overlay-chart.active {
    display: flex !important;
}
.modal.active {
    display: block !important;
}

.modal {
    display: none;
    background: white;
    padding: 20px;
    border-radius: 12px;
    text-align: center;
    width: 500px;
    max-width: 150vw;
    max-height: 75vh;
    overflow-y: auto;
    box-shadow: 0px 10px 30px rgba(0, 0, 0, 0.2);
    animation: fadeIn 0.3s ease-in-out;
    position: relative;
}

.modal-title {
    font-size: 22px;
    font-weight: bold;
    margin-bottom: 10px;
}


.modal-profile-pic {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    border: 4px solid #0073b1;
    margin-bottom: 15px;
}

.modal-footer {
    display: flex;
    justify-content: center;
    padding-top: 15px;
}
.modal-body {
    text-align: left;
    font-size: 16px;
    line-height: 1.6;
    padding: 0 20px;
}


.modal-body p {
    margin: 8px 0;
    color: #333;
}

.modal-body p strong {
    color: #0073b1; /* Highlight key info */
}
.modal.show {
    display: flex !important;
}
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.btn-close-modal {
    background-color: #0073b1;
    color: white;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
    font-size: 16px;
    border-radius: 5px;
    transition: background 0.3s;
    position: relative; /* Adjusted */
}

.btn-close-modal:hover {
    background-color: #005a8d;
}

.close-btn {
    background: none;
    border: none;
    font-size: 26px;
    cursor: pointer;
    color: #000;
    position: absolute;
    top: 15px;
    right: 15px;
    transition: opacity 0.2s ease-in-out;
}

.close-btn:hover {
    opacity: 0.7;
}



.btn-close-modal {
    background-color: #ccc;
    padding: 10px;
    cursor: pointer;
}
/* ✅ Checkbox Styling */
.select-container {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 10px;
}

.select-container input {
    margin-right: 5px;
    transform: scale(1.2); /* Increase checkbox size */
}
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}


`;
document.head.appendChild(adminStyles);
