self.addEventListener('install', event => {
    event.waitUntil(
        caches.open('pwa-app').then(cache => {
            return cache.addAll([
                '/admin/index'
            ]);
        })
    );
});

self.addEventListener('fetch', event => {
    event.respondWith(
        caches.match(event.request).then(response => {
            return response || fetch(event.request);
        })
    );
});
