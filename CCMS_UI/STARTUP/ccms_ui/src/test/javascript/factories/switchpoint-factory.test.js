describe('switchpointFactory', function() {
    var $httpBackend, switchpointFactory;

    beforeEach(module('switchpointFactory'));
    beforeEach(inject(function(_$httpBackend_, _switchpointFactory_) {
        $httpBackend = _$httpBackend_;
        switchpointFactory = _switchpointFactory_;
    }));

    it('calls the light control endpoints', function() {
        $httpBackend.expectGET('/device_conf/lights_on?device_serial_number=DCU-001&device_identifier=2043')
            .respond({ code: 200 });
        switchpointFactory.turnOnLights('?device_serial_number=DCU-001&device_identifier=2043');
        $httpBackend.flush();

        $httpBackend.expectGET('/device_conf/lights_off?device_serial_number=DCU-001&device_identifier=2043')
            .respond({ code: 200 });
        switchpointFactory.turnOffLights('?device_serial_number=DCU-001&device_identifier=2043');
        $httpBackend.flush();
    });
});
