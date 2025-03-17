export default {
  template: `
  <div class="user-dashboard">
      <!-- ✅ Navigation Bar -->
      <div class="nav-bar">
          <img src="/assets/images/logo.jpg" alt="ProLink Logo" class="logo">
          
          <div class="nav-links">
		  <button class="nav-btn active" @click="goToHome"><i class="fas fa-home"></i><span>Home</span></button>
		  <button class="nav-btn" @click="goToNetwork"><i class="fas fa-users"></i><span> My Network</span></button>
		  <button class="nav-btn" @click="goToJobs"><i class="fas fa-briefcase"></i><span> Jobs</span></button>
		  <button class="nav-btn" @click="goToMessaging"><i class="fas fa-comments"></i><span> Messaging</span></button>

              
			  <!-- ✅ User Profile Dropdown -->
			          <div class="profile-dropdown">
			              <img :src="getProfilePictureUrl()" class="profile-pic-nav">
			              <i class="fas fa-caret-down dropdown-arrow"></i>
			              
			              <div class="dropdown-content">
			  
			                  <button @click="logout"><i class="fas fa-sign-out-alt"></i> Logout</button>
			              </div>
			          </div>
          </div>
      </div>

	  <!-- ✅ User Main Section -->
	  <div class="dashboard-content">
	      <!-- ✅ Wrapping Profile and Email Inside a Container -->
	      <div class="user-profile-container">
	          
	          <!-- ✅ User Profile -->
	          <div class="user-profile">
	              <div class="profile-pic-container" @click="uploadProfilePic">
	                  <img 
	                      :src="getProfilePictureUrl()" 
	                      :class="{ 'transparent-pic': !user.profilePictureUrl }" 
	                      class="profile-pic"
	                  >
	                  <div v-if="!user.profilePictureUrl" class="upload-overlay">
	                      <i class="fas fa-plus"></i>
	                  </div>
	                  <input type="file" ref="fileInput" @change="handleProfileUpload" accept="image/*" hidden>
	              </div>

	              <h2 class="user-name">{{ user.fullName || "Loading..." }}</h2>

	              <!-- ✅ Bio Section -->
				  <div class="user-bio-container">
				      <div class="user-bio"
				          contenteditable="true"
				          @focus="handleBioFocus"
				          @keydown.enter.prevent="updateProfile"
				          :class="{ 'empty': !user.bio }">
				          {{ user.bio || "Click to add a bio" }}
				      </div>
				  </div>
	          </div>

	          <!-- ✅ Email Box Positioned Below the Profile -->
	          <div class="user-email-box">
	              <i class="fas fa-envelope email-icon"></i>
	              <span class="user-email">{{ user.email }}</span>
	          </div>

	      </div>

		  <div class="user-details">
		  <!-- ✅ Work Experience Section -->
		  <div class="info-container">
		      <div class="info-section">
		          <h3>Work Experience</h3>
		          <button class="add-btn" @click="showWorkInput = true" v-if="!showWorkInput">+ Add</button>
		      </div>
		      
		      <hr class="info-divider">
		      
		      <ul class="info-list">
		          <li v-for="experience in user.workExperience" :key="experience">• {{ experience }}</li>
		      </ul>

		      <!-- Input Box + Save Button -->
		      <div v-if="showWorkInput">
		          <input v-model="newWorkExperience" type="text" class="input-box" placeholder="Enter work experience">
		          <button class="save-btn" @click="addWorkExperience">Save</button>
		      </div>
		  </div>

		  <!-- ✅ Education Section -->
		  <div class="info-container">
		      <div class="info-section">
		          <h3>Education</h3>
		          <button class="add-btn" @click="showEducationInput = true" v-if="!showEducationInput">+ Add</button>
		      </div>
		      
		      <hr class="info-divider">

		      <ul class="info-list">
		          <li v-for="education in user.education" :key="education">• {{ education }}</li>
		      </ul>

		      <div v-if="showEducationInput">
		          <input v-model="newEducation" type="text" class="input-box" placeholder="Enter education">
		          <button class="save-btn" @click="addEducation">Save</button>
		      </div>
		  </div>



		      <!-- ✅ Skills Section -->
		      <div class="info-container">
		          <div class="info-section">
		              <h3>Skills</h3>
		              <select v-model="selectedSkill" class="dropdown" @change="addSkill">
		                  <option value="" disabled selected>Select a skill</option>
		                  <option v-for="skill in availableSkills" :key="skill" :value="skill">{{ skill }}</option>
		              </select>
		          </div>
				  <hr class="info-divider">
		          <ul class="skills-list">
		              <li v-for="skill in user.skills" :key="skill" class="skill-chip">{{ skill }}</li>
		          </ul>
		      </div>
		  </div>

		  </div>

	  </div>

  `,

  data() {
	return {
	        user: {
	            workExperience: [],
	            education: [],
	            skills: []
	        },
	        newWorkExperience: "",
	        newEducation: "",
	        selectedSkill: "",
	        availableSkills: ["JavaScript", "Python", "React", "Vue.js", "Django","SQL", "AI","Cloud Computing", "Cybersecurity" ,"C++","C"], // Predefined skill options
	        showWorkInput: false,
	        showEducationInput: false,
	    };
	},


  methods: {
	async fetchUserProfile() {
	    try {
	        console.log("Fetching user profile...");

	        if (!this.user.username) {
	            console.error("Username is not set.");
	            return;
	        }
			
			// Retrieve the JWT token from localStorage
			        const token = localStorage.getItem("jwtToken");
			        if (!token) {
			            console.error("Authorization token not found. Please log in again.");
			            window.location.href = "/login"; // Redirect to login page
			            return;
			        }

			const res = await fetch(`/api/users/profile/${this.user.username}`, {
			            method: "GET", // Explicitly specify the method (optional)
			            headers: {
			                "Authorization": `Bearer ${token}`, // Include the token in the headers
			                "Content-Type": "application/json" // Optional, depending on your API
			            }
			        });

	        if (!res.ok) {
	            throw new Error(`Failed to fetch user profile. Status: ${res.status}`);
	        }

	        this.user = await res.json();
	        console.log("User data loaded:", this.user);

	        if (this.user.profilePictureUrl && !this.user.profilePictureUrl.startsWith("http")) {
	            this.user.profilePictureUrl = `http://localhost:8080${this.user.profilePictureUrl}`;
	        }
	    } catch (error) {
	        console.error("Error fetching user data:", error);
	    }
	},
	async addWorkExperience() {
	    if (!this.newWorkExperience.trim()) return;

	    
	    if (!this.user.workExperience) {
	        this.user.workExperience = [];
	    }

	    
	    this.user.workExperience.push(this.newWorkExperience);

	    try {
	        const response = await fetch(`/api/users/profile/update/${this.user.username}`, {
	            method: "PUT",
	            headers: { "Content-Type": "application/json" },
	            body: JSON.stringify({ workExperience: this.user.workExperience }),
	        });

	        if (!response.ok) {
	            throw new Error("Failed to update work experience.");
	        }

	        console.log("Work experience updated successfully.");
	    } catch (error) {
	        console.error("Error updating work experience:", error);
	    }

	    
	    this.showWorkInput = false;
	    this.newWorkExperience = "";
	},

	async addEducation() {
	    if (!this.newEducation.trim()) return;

	    
	    if (!this.user.education) {
	        this.user.education = [];
	    }

	    
	    this.user.education.push(this.newEducation);

	    try {
	        const response = await fetch(`/api/users/profile/update/${this.user.username}`, {
	            method: "PUT",
	            headers: { "Content-Type": "application/json" },
	            body: JSON.stringify({ education: this.user.education }),
	        });

	        if (!response.ok) {
	            throw new Error("Failed to update education.");
	        }

	        console.log("Education updated successfully.");
	    } catch (error) {
	        console.error("Error updating education:", error);
	    }

	    
	    this.showEducationInput = false;
	    this.newEducation = "";
	},

		    async addSkill() {
		        if (!this.selectedSkill || this.user.skills.includes(this.selectedSkill)) return;
		        this.user.skills.push(this.selectedSkill);
		        await this.updateUserProfile("skills", this.user.skills);
		        this.selectedSkill = "";
		    },

			async updateUserProfile(field, value) {
			    const token = localStorage.getItem("jwtToken"); // Get token from localStorage

			    try {
			        const response = await fetch(`/api/users/profile/update/${this.user.username}`, {
			            method: "PUT",
			            headers: {
			                "Authorization": `Bearer ${token}`,
			                "Content-Type": "application/json"
			            },
			            body: JSON.stringify({ [field]: value })
			        });

			        if (!response.ok) {
			            throw new Error(`Failed to update ${field}`);
			        }

			        console.log(`${field} updated successfully!`);
			    } catch (error) {
			        console.error(`Error updating ${field}:`, error);
			    }
			},
			getProfilePictureUrl() {
			    if (this.user.profilePictureUrl) {
			        return this.user.profilePictureUrl.startsWith("http")
			            ? this.user.profilePictureUrl
			            : `http://localhost:8080${this.user.profilePictureUrl}`;
			    }
			    return "/assets/images/admin-avatar.png"; // Default avatar
			},
	handleBioFocus(event) {
	    if (event.target.innerText.trim() === "Click to add a bio") {
	        event.target.innerText = "";
	    }
	},
	updateField(field, event) {
	    const newValue = event.target.innerText.trim();
	    
	    if (!newValue) return; 

	   
	    const updatedValue = field === "skills" ? newValue.split(",").map(skill => skill.trim()) : newValue;

	    fetch(`/api/users/profile/update/${this.user.username}`, {
	        method: "PUT",
	        headers: { "Content-Type": "application/json" },
	        body: JSON.stringify({ [field]: updatedValue }) 
	    })
	    .then(response => response.json())
	    .then(data => {
	        console.log(`${field} updated successfully:`, data);
	        this.user[field] = updatedValue; // 
	    })
	    .catch(error => console.error(`Error updating ${field}:`, error));
	},

	updateProfile(event) {
	    const field = event.target.classList.contains("user-name") ? "fullName" : "bio";
	    const newValue = event.target.innerText.trim();
	    
	    if (!newValue) return; 

	    fetch(`/api/users/profile/update/${this.user.username}`, {
	        method: "PUT",
	        headers: { "Content-Type": "application/json" },
	        body: JSON.stringify({ [field]: newValue }) 
	    })
	    .then(response => response.json())
	    .then(data => {
	        console.log(`${field} updated successfully:`, data);
	        this.user[field] = newValue; 
	    })
	    .catch(error => console.error(`Error updating ${field}:`, error));
	},

      uploadProfilePic() {
          this.$refs.fileInput.click();
      },

	  goToNetwork() {
	          window.location.href = "/network";
	      },

      goToJobs() {
	          window.location.href = "/jobs"; 
	      },

	  goToMessaging() {
	          window.location.href = "/messaging";
	      },

	      
		  logout() {
		      localStorage.removeItem("jwtToken"); // Clear token
		      localStorage.removeItem("username"); // Clear username
		      localStorage.removeItem("role"); // Clear role
		      window.location.href = "/login"; // Redirect to login page
		  },

		async handleProfileUpload(event) {
		    const file = event.target.files[0];
		    if (!file) return;

		    const formData = new FormData();
		    formData.append("file", file);

		    const token = localStorage.getItem("jwtToken"); // Get token from localStorage

		    try {
		        const res = await fetch(`/api/users/profile/upload/${this.user.username}`, {
		            method: "POST",
		            headers: {
		                "Authorization": `Bearer ${token}` // Add token to headers
		            },
		            body: formData
		        });

		        const data = await res.json();

		        if (res.ok) {
		            this.user.profilePictureUrl = `${data.profilePictureUrl}?t=${new Date().getTime()}`;
		        } else {
		            console.error("Upload failed:", data);
		        }
		    } catch (error) {
		        console.error("Error uploading profile picture:", error);
		    }
		},

      logout() {
          window.location.href = "/login";
      }
  },

  mounted() {
      this.user.username = localStorage.getItem("username");
      console.log("Username from localStorage:", this.user.username); // Debugging
      this.fetchUserProfile();
  }
};

// ✅ Scoped CSS for Navbar (Exact Look)
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

/* ✅ Dashboard Content */
.dashboard-content {
    display: flex;
    gap: 30px;
    padding: 30px;
    align-items: flex-start;
}

/* ✅ User Profile Container */
.user-profile-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
    background: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    width: 320px;
	align-items: centre;
}

.profile-pic-container {
    position: relative;
    display: inline-block;
    cursor: pointer;
	align-items: centre;
}
/* ✅ Separator Line */
.info-divider {
    border: none;
    border-top: 1px solid #ddd; /* Light grey line */
    margin: 19px 0; /* Space above and below */
}


.profile-pic {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    border: 3px solid #0073b1;
}

.transparent-pic {
    opacity: 0.5;
}

.upload-overlay {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba(0, 0, 0, 0.5);
    border-radius: 50%;
    width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.upload-overlay i {
    color: white;
    font-size: 20px;
}

.profile-pic-container:hover .upload-overlay {
    background: rgba(0, 0, 0, 0.7);
}

.user-name {
    font-size: 20px;
    font-weight: bold;
    margin-top: 10px;
}

/* ✅ Bio Section */
.user-bio-container {
    display: flex;
    justify-content: center;
    background: white;
    padding: 10px;
    border-radius: 5px;
}

.user-bio {
    min-height: 30px;
    font-size: 14px;
    color: grey;
    width: 100%;
    outline: none;
    background: white;
    border: none;
    text-align: center;
}

.user-bio.empty {
    color: #aaa;
}

/* ✅ User Email */
.user-email-box {
    display: flex;
    align-items: center;
    background: white;
    padding: 12px;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    justify-content: center;
    gap: 10px;
    width: 280px;
}

.email-icon {
    color: #0073b1;
    font-size: 16px;
}

/* ✅ General Container Fix */
.user-details {
    display: flex;
    flex-direction: column;
    gap: 15px;
    flex: 2;
}

/* ✅ Properly Encloses Each Section in a White Box */
.info-container {
    background: white;
    padding: 15px;
    border-radius: 5px;
    border: 1px solid #ddd;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    margin-bottom: 15px;
}

/* ✅ Fixes Heading + Add Button Layout */
.info-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    font-size: 16px;
    margin-bottom: 10px;
}

/* ✅ Ensures List Items Stay Inside the Box */
/* ✅ Fix: Prevent List Items from Breaking into Separate Lines */
.info-list {
    padding: 5px;
    margin: 0;
    list-style: none;
    font-size: 18px;
    color: grey;
    gap: 5px; /* ✅ Adds slight spacing between words */
}

.info-list li {
    
    padding: 5px; /* ✅ Removes unnecessary padding */
}


/* ✅ "+ Add" Button */
.add-btn {
    background: none;
    border: none;
    color: #0073b1;
    font-size: 14px;
    cursor: pointer;
    font-weight: bold;
}

.add-btn:hover {
    text-decoration: underline;
}

/* ✅ Fixing Input Box */
.input-box {
    width: calc(100% - 20px);
    padding: 10px;
    margin-top: 8px;
    border-radius: 5px;
    border: 1px solid #ddd;
    font-size: 14px;
    background: white;
}

/* ✅ Save Button */
.save-btn {
    background: #0073b1;
    color: white;
    padding: 6px 12px;
    border: none;
    border-radius: 5px;
    font-size: 14px;
    cursor: pointer;
    font-weight: bold;
    margin-top: 5px;
}

.save-btn:hover {
    background: #005f99;
}

/* ✅ Skills Section */
.dropdown {
    width: 90%;
    padding: 14px;
    margin-top: 5px;
    border-radius: 10px;
    border: 3px solid #ddd;
    font-size: 14px;
    background: white;
}

.skills-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    padding: 10px;
}

.skill-chip {
    background: #f3f3f3;
    padding: 5px 10px;
    border-radius: 15px;
    font-size: 16px;
    color: #333;
    border: 1px solid #ddd;
}

.skill-chip:hover {
    background: #e0e0e0;
}

`;

document.head.appendChild(userStyles);
