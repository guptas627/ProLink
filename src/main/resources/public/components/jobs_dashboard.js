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
              <button class="nav-btn" @click="goToNetwork"><i class="fas fa-users"></i><span> My Network</span></button>
              <button class="nav-btn active" @click="goToJobs"><i class="fas fa-briefcase"></i><span> Jobs</span></button>
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

      <!-- Job Dashboard Layout -->
      <div class="job-dashboard">
          <!-- Left Side: Create a Job -->
          <div class="create-job">
              <h2>Create a Job</h2>
              <form @submit.prevent="postJob">
                  <input type="text" v-model="newJob.title" placeholder="Job title *" required>
                  <textarea v-model="newJob.description" placeholder="Job description *" required></textarea>
                  <input type="text" v-model="newJob.location" placeholder="Location *" required>
                  <button type="submit">Save</button>
              </form>

              <!-- Sort Options -->
              <div class="sort-options">
                  <label>Sort by:</label>
                  <input type="radio" id="date" value="date" v-model="sortOption">
                  <label for="date">Date created</label>
                  <input type="radio" id="relevant" value="relevant" v-model="sortOption">
                  <label for="relevant">Relevant to you</label>
              </div>
          </div>

          <!-- Right Side: Jobs List -->
          <div class="jobs-list">
              <h2>Jobs</h2>
			  <div v-for="job in sortedJobs" :key="job.id" class="job-card">
			      <h3>{{ job.title }}</h3>
				  <hr class="info-divider">
			      <p>{{ job.description }}</p>
			      <p><strong>Location:</strong> {{ job.location }}</p>
				  <p><strong>Applicants so far:</strong> {{ job.applicationCount }}</p>
			      <p><strong>Posted by:</strong> <span class="posted-by">{{ job.postedBy.fullName }}</span></p>
			      
			      

				  <button class="apply-btn" @click="applyForJob(job.id)">+ Apply</button>
			  </div>

          </div>
      </div>
  </div>
  `,

  data() {
    return {
      newJob: {
        title: "",
        description: "",
        location: "",
		skills: [],
      },
	  availableSkills: ["JavaScript", "Python", "React", "Vue.js", "Django","SQL", "AI","Cloud Computing", "Cybersecurity" ,"C++","C"],
      jobs: [], // ✅ Ensure this is an array and not undefined
      sortOption: "date",
      currentUser: null,
	  showToast: false, 
	  toastMessage: "",
    };
  
  },

  computed: {
    sortedJobs() {
      if (!Array.isArray(this.jobs)) return []; // ✅ Ensure it's an array
      if (this.sortOption === "relevant") {
        return [...this.jobs].reverse();
      }
      return this.jobs;
    }
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
	        if (!response.ok) throw new Error("Failed to fetch user profile");

	        this.currentUser = await response.json();

	        if (!this.currentUser || !this.currentUser.username) {
	            console.error("Invalid user data received.");
	            return;
	        }

	        this.fetchJobs();
	    } catch (error) {
	        console.error("Error fetching user profile:", error);
	    }
	},

	async fetchJobs() {
	    try {
	        if (!this.currentUser || !this.currentUser.username) {
	            console.error("User not logged in, cannot fetch jobs.");
	            return;
	        }

	        const token = localStorage.getItem("jwtToken");
	        if (!token) {
	            console.error("No JWT token found in localStorage.");
	            window.location.href = "/login";
	            return;
	        }

	        const response = await fetch(`/api/jobs`, {
	            headers: {
	                "Authorization": `Bearer ${token}`,
	            },
	        });
	        const jobData = await response.json();
	        console.log("Fetched Jobs:", jobData);

	        if (jobData._embedded && Array.isArray(jobData._embedded.jobList)) {
	            this.jobs = await Promise.all(
	                jobData._embedded.jobList.map(async (job) => {
	                    const applicationCount = await this.fetchApplicationCount(job.id);
	                    return { ...job, applicationCount };
	                })
	            );
	        } else {
	            this.jobs = [];
	        }
	    } catch (error) {
	        console.error("Error fetching jobs:", error);
	        this.jobs = [];
	    }
	},
	async fetchApplicationCount(jobId) {
	    try {
	        const token = localStorage.getItem("jwtToken");
	        if (!token) {
	            console.error("No JWT token found in localStorage.");
	            window.location.href = "/login";
	            return 0;
	        }

	        const response = await fetch(`/api/applications/count/${jobId}`, {
	            headers: {
	                "Authorization": `Bearer ${token}`,
	            },
	        });
	        if (!response.ok) throw new Error("Failed to fetch application count");
	        return await response.json();
	    } catch (error) {
	        console.error(`Error fetching application count for job ${jobId}:`, error);
	        return 0; // Return 0 if there's an error
	    }
	},
	async postJob() {
	    if (!this.newJob.title || !this.newJob.description || !this.newJob.location) return;

	    try {
	        const token = localStorage.getItem("jwtToken");
	        if (!token) {
	            console.error("No JWT token found in localStorage.");
	            window.location.href = "/login";
	            return;
	        }

	        const response = await fetch(`/api/jobs/${this.currentUser.username}/post`, {
	            method: "POST",
	            headers: {
	                "Authorization": `Bearer ${token}`,
	                "Content-Type": "application/json",
	            },
	            body: JSON.stringify({
	                title: this.newJob.title,
	                description: this.newJob.description,
	                location: this.newJob.location,
	                skillsRequired: this.newJob.skills,
	            }),
	        });

	        if (!response.ok) throw new Error("Failed to post job");

	        console.log("✅ Job posted successfully! Fetching updated job list...");

	        this.newJob = { title: "", description: "", location: "", skills: [] }; // Clear form

	        await this.fetchJobs();
	    } catch (error) {
	        console.error("❌ Error posting job:", error);
	    }
	},

	async applyForJob(jobId) {
	    if (!this.currentUser || !this.currentUser.username) {
	        console.error("User not logged in. Cannot apply for job.");
	        return;
	    }

	    try {
	        const token = localStorage.getItem("jwtToken");
	        if (!token) {
	            console.error("No JWT token found in localStorage.");
	            window.location.href = "/login";
	            return;
	        }

	        const response = await fetch(`/api/applications/${this.currentUser.username}/apply/${jobId}`, {
	            method: "POST",
	            headers: {
	                "Authorization": `Bearer ${token}`,
	                "Content-Type": "application/json",
	            },
	            body: JSON.stringify({ resumeUrl: "", coverLetter: "" })
	        });

	        if (!response.ok) throw new Error("Failed to apply for job");

	        this.showToastMessage("Applied successfully! ✅");
	        this.fetchJobs();
	    } catch (error) {
	        console.error("Error applying for job:", error);
	        this.showToastMessage("❌ Error applying for job");
	    }
	},

	// Toast function
	showToastMessage(message) {
	    this.toastMessage = message;
	    this.showToast = true;

	    this.$nextTick(() => {
	        const toastElement = document.querySelector(".toast");
	        if (toastElement) {
	            toastElement.classList.add("show"); // Ensure toast is displayed
	        }
	    });

	    setTimeout(() => {
	        this.showToast = false;
	        this.$nextTick(() => {
	            const toastElement = document.querySelector(".toast");
	            if (toastElement) {
	                toastElement.classList.remove("show"); // Hide after 3 seconds
	            }
	        });
	    }, 3000);
	},
	
    formatTimestamp(timestamp) {
      return new Date(timestamp).toLocaleString();
    },

    getProfilePictureUrl() {
      return this.currentUser?.profilePictureUrl || "/assets/images/admin-avatar.png";
    },

    goToHome() { window.location.href = "/users"; },
    goToNetwork() { window.location.href = "/network"; },
    goToMessaging() { window.location.href = "/messaging"; },
	logout() {
	    localStorage.removeItem("jwtToken");
	    localStorage.removeItem("username");
	    window.location.href = "/login";
	},
  },

  mounted() {
    this.fetchCurrentUser();
  },
};

const userStyles = document.createElement("style");
userStyles.innerHTML = `
.job-dashboard {
    display: flex;
    justify-content: space-between;
    padding: 20px;
    background-color: #f3f2ef !important; /* Ensure full width */
    width: 100%;
    min-height: 100vh; /* Extend to full screen height */
}


  /* Create a Job Section */
  .create-job {
	  width: 410px;  /* Fixed width */
	  height: 450px;
      background: #fff;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
	  margin-top: 60px; /* Adds space from the top */
	  margin-left: 10px;
	  margin-right: auto;
  }

  .create-job h2 {
      font-size: 20px;
      margin-bottom: 20px;
	  text-align: center;
  }

  .create-job form {
      display: flex;
      flex-direction: column;
  }

  .create-job input,
  .create-job textarea {
      width: 90%; /* Reduce width from 100% to 80% */
      max-width: 380px; /* Set a max width to prevent large expansion */
      padding: 8px;
      margin-bottom: 10px;
      border: 1px solid #ccc;
      border-radius: 5px;
      display: block; /* Ensures proper alignment */
      margin-left: auto;
      margin-right: auto; /* Centers the text fields */
  }


  .create-job button {
	width: 100%;
	max-width: 380px;
      padding: 10px;
      border: none;
      background: #0073b1;
      color: white;
      font-weight: bold;
      cursor: pointer;
	  border-radius: 5px;
	  margin-left: auto;
	        margin-right: auto; 
  }

  /* Sort Options */
  .sort-options {
      margin-top: 45px;
      font-size: 14px;
      display: flex; /* Align items in one line */
      align-items: center; /* Ensure vertical alignment */
      gap: 10px; /* Adjust spacing for better readability */
      justify-content: center; /* Center the content */
  }

  /* Style for the "Sort by:" label */
  .sort-options label:first-of-type {
      margin-right: 5px; /* Adds spacing after "Sort by:" */
  }

  /* Improve radio button alignment */
  .sort-options input[type="radio"] {
      appearance: none;
      width: 16px;
      height: 16px;
      border: 2px solid #999;
      border-radius: 50%;
      outline: none;
      cursor: pointer;
      position: relative;
  }

  /* When radio button is selected */
  .sort-options input[type="radio"]:checked {
      border-color: #d63384; /* Pink color */
  }

  /* Inner circle when checked */
  .sort-options input[type="radio"]:checked::before {
      content: "";
      width: 8px;
      height: 8px;
      background-color: #d63384; /* Pink fill */
      border-radius: 50%;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
  }



  /* Jobs List */
  .jobs-list {
      width: 65%;
  }
  .info-divider {
      border: none;
      border-top: 1px solid #ddd; /* Light grey line */
      margin: 19px 0; /* Space above and below */
  }
  .jobs-list h2 {
      text-align: center; /* Center the text horizontally */
  }


  .job-card {
      background: #fff;
      padding: 25px;
      margin-bottom: 10px;
      border-radius: 10px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }

  .job-card h3 {
      font-size: 28px;
      margin-bottom: 5px;
  }

  .posted-by {
      color: #0073b1;
      font-weight: bold;
  }

  .apply-btn {
      background: #0073b1;
      color: white;
      padding: 10px 14px;
      border: none;
      cursor: pointer;
      border-radius: 8px; /* Curves the corners */
  }
  /* Toast Notification */
  /* Toast Notification */
  .toast {
      position: fixed;
      bottom: 50px;
      right: 20px;
      background: #444; /* Default Background */
      color: white;
      padding: 12px 20px;
      border-radius: 10px;
      box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
      font-size: 14px;
      z-index: 9999;
      opacity: 0;
      visibility: hidden;
      transition: opacity 0.5s ease-in-out, visibility 0.5s ease-in-out;
  }

  /* ✅ Set Default Toast to a Lighter Background */
  .toast:not(.success):not(.error) {
      background: #90EE90; /* Light Red (Error-like) */
      color: #721c24;
  }

  /* ✅ Success and Error Toasts */
  .toast.success {
      background: #4CAF50; /* Green */
      color: white;
  }

  .toast.error {
      background: #F44336; /* Red */
      color: white;
  }

  /* ✅ When toast is visible */
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
