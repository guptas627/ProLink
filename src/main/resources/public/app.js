const { createApp } = Vue;

import Login from "./components/login.js";
import Register from "./components/register.js";
import Navbar from "./components/navbar.js";
import AdminDashboard from "./components/admin-dashboard.js";
import UserDashboard from "./components/user-dashboard.js";
import NetworkComponent from "./components/my_network.js";
import JobComponent from "./components/jobs_dashboard.js";
import MessageComponent from "./components/messaging_dashboard.js";



const routes = {
    "/": Login,
    "/login": Login,
    "/register": Register,
	"/admin": AdminDashboard,
	"/users": UserDashboard,
	"/network": NetworkComponent,
	"/jobs" : JobComponent,
	"/messaging" : MessageComponent,
};

const App = {
    data() {
        return {
            currentRoute: window.location.pathname,
        };
    },
    computed: {
        ViewComponent() {
            return routes[this.currentRoute] || Login;
        }
    },
    template: `
      <Navbar />
      <component :is="ViewComponent" />
    `,
};

const app = createApp(App);
app.component("Navbar", Navbar);
app.mount("#app");
