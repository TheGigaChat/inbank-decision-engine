FROM node:20-alpine

WORKDIR /app

ARG API_BASE_URL=http://localhost:8080
ENV API_BASE_URL=$API_BASE_URL

COPY package*.json ./
RUN npm install

COPY . .

RUN npm run build

EXPOSE 3000

CMD ["npm", "run", "start"]
