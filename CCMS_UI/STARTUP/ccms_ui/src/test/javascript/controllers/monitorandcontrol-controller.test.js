describe('monitorandcontrolControllers', function() {
    var $scope, $rootScope, $controller, $httpBackend, monitorandcontrolFactory;
    var mockState, mockConfig, mockStateParams;
    var mockInform, mockModal;

    beforeEach(module('inform'));
    beforeEach(module('monitorandcontrolControllers'));
    beforeEach(module('monitorandcontrolFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _monitorandcontrolFactory_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        monitorandcontrolFactory = _monitorandcontrolFactory_;
        $controller = _$controller_;

        mockInform = { add: jasmine.createSpy('inform.add') };
        mockModal = { open: jasmine.createSpy('modal.open').and.returnValue({ result: { then: function(){} } }) };

        mockConfig = { districts: [{ state: 'Guntur-17', code: 'Guntur-17' }] };
        mockStateParams = {};

        $rootScope.privilege = {
            district: 'ALL',
            mandal: 'ALL',
            gp: 'ALL',
            monitor_and_controller: true,
            history: true,
            event: true,
            switching_point_summary: true,
            operational_hour: true,
            light_status: true,
            schedule: true,
            settings: true,
            default_settings: true,
            filter: true,
            node: true,
            dcu: true,
            user: true
        };

        $httpBackend.whenGET('/CCMS/dcu/dcu_name_list?district=ALL&mandal=ALL&gp=ALL')
            .respond([{ name: 'DCU-001', id: 'dcu1' }]);
        $httpBackend.whenGET('/CCMS/dashboard/instant_data_filter?district=ALL&mandal=ALL&gp=ALL&page=1&size=10')
            .respond([]);
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    function createController() {
        $controller('monitorandcontrolListControllers', {
            $scope: $scope,
            $rootScope: $rootScope,
            monitorandcontrolFactory: monitorandcontrolFactory,
            config: mockConfig,
            $state: { go: jasmine.createSpy('$state.go') },
            $stateParams: mockStateParams,
            inform: mockInform,
            $modal: mockModal
        });
    }

    describe('$scope.turn_on_light', function() {
        it('should call turnOnLights when light_status is 0', function() {
            createController();
            var obj = {
                dcu_details: {
                    gateway_serial_number: '1905HY1P1C009534',
                    serial_number: '2043',
                    light_status: 0
                }
            };

            $httpBackend.expectGET('/CCMS/device_conf/lights_on?device_serial_number=1905HY1P1C009534&device_identifier=2043')
                .respond({ code: 200, message: 'success' });

            $scope.turn_on_light(obj);
        });

        it('should call turnOffLights when light_status is 1', function() {
            createController();
            var obj = {
                dcu_details: {
                    gateway_serial_number: '1905HY1P1C009534',
                    serial_number: '2043',
                    light_status: 1
                }
            };

            $httpBackend.expectGET('/CCMS/device_conf/lights_off?device_serial_number=1905HY1P1C009534&device_identifier=2043')
                .respond({ code: 200, message: 'success' });

            $scope.turn_on_light(obj);
        });

        it('should handle empty dcu_details gracefully', function() {
            createController();
            var obj = { dcu_details: null };
            expect(function() {
                $scope.turn_on_light(obj);
            }).toThrow();
        });

        it('should handle missing serial number', function() {
            createController();
            var obj = {
                dcu_details: {
                    gateway_serial_number: '',
                    serial_number: '',
                    light_status: 0
                }
            };

            $httpBackend.expectGET('/CCMS/device_conf/lights_on?device_serial_number=&device_identifier=')
                .respond({ code: 200, message: 'success' });

            $scope.turn_on_light(obj);
        });
    });
});
