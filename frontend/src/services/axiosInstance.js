import { getAccessToken } from "./authService";

const { default: axios } = require("axios");

const authed = axios.create({
    baseURL: "/api"
});

const unauthed = axios.create({
    baseURL: "/api"
});

authed.interceptors.request.use(
    function (request) {
        request.headers = {
            Authorization: "Bearer " + getAccessToken()
        }
        return request;
    }
)

export {authed, unauthed};

