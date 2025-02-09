from node:23-bullseye as build

workdir /usr/src/app

copy package.json package-lock.json ./

run --mount=type=cache,target=/usr/src/app/.npm \
    npm set cache /usr/src/app/.npm && \
    npm install --force

copy . .

run npm run build

from nginxinc/nginx-unprivileged:alpine-slim

expose 8080

copy --from=build /usr/src/app/build /usr/share/nginx/html
