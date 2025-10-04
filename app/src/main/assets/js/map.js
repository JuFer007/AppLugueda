document.addEventListener('DOMContentLoaded', function () {
    // 1. Inicializar el mapa
    const map = L.map('map');

    // 2. Añadir la capa de mapa base de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // 3. Ícono personalizado para el autobús
    const busIcon = L.divIcon({
        html: '<i class="fa-solid fa-bus"></i>',
        className: 'bus-marker-icon',
        iconSize: [30, 30],
        iconAnchor: [15, 15]
    });

    // 4. Definir las rutas de ida (verde) y vuelta (naranja)
    const forwardRoute = [
        [-6.760277, -79.863055], // USAT
        [-6.7618889, -79.8629225], // Hospital Regional Lambayeque
        [-6.7631644, -79.8616334], // Mall Aventura
        [-6.763611, -79.863055], // UTP
        [-6.7720, -79.8400],      // Banco de la Nación
        [-6.7705, -79.8375],      // Mercado Modelo
        [-6.77857, -79.83264]     // Real Plaza Chiclayo
    ];

    const backwardRoute = [
        [-6.77857, -79.83264],
        [-6.785, -79.835],
        [-6.790, -79.845],
        [-6.780, -79.860],
        [-6.765, -79.865],
        [-6.760277, -79.863055]
    ];

    // 5. Dibujar las polilíneas de las rutas
    const forwardPolyline = L.polyline(forwardRoute, { color: '#00733c', weight: 5, opacity: 0.8 }).addTo(map);
    const backwardPolyline = L.polyline(backwardRoute, { color: '#dc3545', weight: 5, opacity: 0.8 }).addTo(map);

    // 6. Centrar el mapa en el centro de la ruta y fijar zoom
    const bounds = forwardPolyline.getBounds();
    const center = bounds.getCenter();
    map.setView(center, 14); // Zoom fijo 14

    // 7. Simulación de autobuses
    const buses = [];
    const numberOfBuses = 4;
    const speed = 0.002;

    for (let i = 0; i < numberOfBuses; i++) {
        const isForward = i % 2 === 0;
        const route = isForward ? forwardRoute : backwardRoute;

        let startSegment = 0;
        if (i === 2) startSegment = Math.floor((forwardRoute.length - 1) / 2);
        else if (i === 3) startSegment = Math.floor((backwardRoute.length - 1) / 2);

        const busData = {
            id: i + 1,
            plate: `ABC-${123 + i}`,
            speed: Math.floor(Math.random() * (50 - 30) + 30),
            marker: L.marker(route[startSegment], { icon: busIcon }).addTo(map),
            route: route,
            segmentIndex: startSegment,
            progress: 0
        };

        const popupContent = `
            <b>Bus ${busData.id}</b><br>
            Placa: ${busData.plate}<br>
            Velocidad: ${busData.speed} km/h`;
        busData.marker.bindPopup(popupContent);
        buses.push(busData);
    }

    function updateBusPositions() {
        buses.forEach(bus => {
            bus.progress += speed;

            if (bus.progress >= 1) {
                bus.progress = 0;
                bus.segmentIndex++;
                if (bus.segmentIndex >= bus.route.length - 1) {
                    bus.segmentIndex = 0;
                    bus.route = (bus.route === forwardRoute) ? backwardRoute : forwardRoute;
                }
            }

            const currentSegment = bus.route.slice(bus.segmentIndex, bus.segmentIndex + 2);
            const startPoint = currentSegment[0];
            const endPoint = currentSegment[1];

            const lat = startPoint[0] + (endPoint[0] - startPoint[0]) * bus.progress;
            const lng = startPoint[1] + (endPoint[1] - startPoint[1]) * bus.progress;

            bus.marker.setLatLng([lat, lng]);

            const angle = Math.atan2(endPoint[0] - startPoint[0], endPoint[1] - startPoint[1]) * 180 / Math.PI;
            bus.marker.setRotationAngle(angle + 90); // opcional
        });
    }

    setInterval(updateBusPositions, 50);
});
