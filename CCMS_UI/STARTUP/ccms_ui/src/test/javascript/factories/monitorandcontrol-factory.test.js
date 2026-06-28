describe('monitorandcontrolFactory', function() {
    var $httpBackend, monitorandcontrolFactory;

    beforeEach(module('monitorandcontrolFactory'));

    beforeEach(inject(function(_$httpBackend_, _monitorandcontrolFactory_) {
        $httpBackend = _$httpBackend_;
        monitorandcontrolFactory = _monitorandcontrolFactory_;
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    describe('turnOnLights', function() {
        it('should call /CCMS/device_conf/lights_on with serial and identifier', function() {
            $httpBackend.expectGET('/CCMS/device_conf/lights_on?device_serial_number=1905HY1P1C009534&device_identifier=2043')
                .respond({ code: 200, message: 'success' });

            monitorandcontrolFactory.turnOnLights('?device_serial_number=1905HY1P1C009534&device_identifier=2043');
        });

        it('should return status response', function() {
            var response = { code: 200, message: 'success' };
            $httpBackend.whenGET('/CCMS/device_conf/lights_on?device_serial_number=X&device_identifier=Y')
                .respond(response);

            var result;
            monitorandcontrolFactory.turnOnLights('?device_serial_number=X&device_identifier=Y')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(response);
        });

        it('should handle empty params', function() {
            $httpBackend.expectGET('/CCMS/device_conf/lights_on?device_serial_number=&device_identifier=')
                .respond({ code: 200 });

            monitorandcontrolFactory.turnOnLights('?device_serial_number=&device_identifier=');
        });

        it('should handle server error', function() {
            $httpBackend.whenGET('/CCMS/device_conf/lights_on?device_serial_number=X&device_identifier=Y')
                .respond(500);

            monitorandcontrolFactory.turnOnLights('?device_serial_number=X&device_identifier=Y');
        });
    });

    describe('turnOffLights', function() {
        it('should call /CCMS/device_conf/lights_off with serial and identifier', function() {
            $httpBackend.expectGET('/CCMS/device_conf/lights_off?device_serial_number=1905HY1P1C009534&device_identifier=2043')
                .respond({ code: 200, message: 'success' });

            monitorandcontrolFactory.turnOffLights('?device_serial_number=1905HY1P1C009534&device_identifier=2043');
        });

        it('should return status response', function() {
            var response = { code: 200, message: 'success' };
            $httpBackend.whenGET('/CCMS/device_conf/lights_off?device_serial_number=X&device_identifier=Y')
                .respond(response);

            var result;
            monitorandcontrolFactory.turnOffLights('?device_serial_number=X&device_identifier=Y')
                .then(function(data) { result = data.data; });
            $httpBackend.flush();

            expect(result).toEqual(response);
        });

        it('should handle empty params', function() {
            $httpBackend.expectGET('/CCMS/device_conf/lights_off?device_serial_number=&device_identifier=')
                .respond({ code: 200 });

            monitorandcontrolFactory.turnOffLights('?device_serial_number=&device_identifier=');
        });
    });

    describe('getAllHandShake', function() {
        it('should call /CCMS/dashboard/instant_data_filter with params', function() {
            $httpBackend.expectGET('/CCMS/dashboard/instant_data_filter?distrtict=ALL&mandal=ALL&gp=ALL&page=1&size=10')
                .respond([]);

            monitorandcontrolFactory.getAllHandShake('?distrtict=ALL&mandal=ALL&gp=ALL', 1, 10);
        });
    });
});
