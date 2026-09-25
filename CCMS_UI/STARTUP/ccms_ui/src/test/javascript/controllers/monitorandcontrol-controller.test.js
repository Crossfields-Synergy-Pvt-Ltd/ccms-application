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

        $httpBackend.whenGET('/dashboard/count?district=ALL&mandal=ALL&gp=ALL&village=ALL')
            .respond({ total_devices: 10, mcb_trip_count: 0 });
        $httpBackend.whenPOST('/dashboard/instant_data_filter?district=ALL&mandal=ALL&gp=ALL&village=ALL&page=0&size=50')
            .respond([]);
        $httpBackend.whenGET('/filter/get_mandal?district=ALL')
            .respond([]);
        $httpBackend.whenGET('/filter/get_gp?mandal=ALL')
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

    describe('$scope.toggle_light', function() {
        it('should call turnOnLights when light_status is 0 and update status to 1 on success', function() {
            createController();
            var obj = {
                dcu_details: {
                    gateway_serial_number: '1905HY1P1C009534',
                    serial_number: '2043',
                    light_status: 0
                }
            };

            $httpBackend.expectGET('/device_conf/lights_on?device_serial_number=1905HY1P1C009534&device_identifier=2043')
                .respond({ code: 200, message: 'success' });

            $scope.toggle_light(obj);
            $httpBackend.flush();

            expect(obj.dcu_details.light_status).toBe(1);
        });

        it('should call turnOffLights when light_status is 1 and update status to 0 on success', function() {
            createController();
            var obj = {
                dcu_details: {
                    gateway_serial_number: '1905HY1P1C009534',
                    serial_number: '2043',
                    light_status: 1
                }
            };

            $httpBackend.expectGET('/device_conf/lights_off?device_serial_number=1905HY1P1C009534&device_identifier=2043')
                .respond({ code: 200, message: 'success' });

            $scope.toggle_light(obj);
            $httpBackend.flush();

            expect(obj.dcu_details.light_status).toBe(0);
        });

        it('should not update light_status on API failure', function() {
            createController();
            var obj = {
                dcu_details: {
                    gateway_serial_number: '1905HY1P1C009534',
                    serial_number: '2043',
                    light_status: 0
                }
            };

            $httpBackend.expectGET('/device_conf/lights_on?device_serial_number=1905HY1P1C009534&device_identifier=2043')
                .respond(500);

            $scope.toggle_light(obj);
            $httpBackend.flush();

            expect(obj.dcu_details.light_status).toBe(0);
        });

        it('should ignore an empty dcu_details object gracefully', function() {
            createController();
            expect(function() { $scope.toggle_light({ dcu_details: null }); }).not.toThrow();
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

            $httpBackend.expectGET('/device_conf/lights_on?device_serial_number=&device_identifier=')
                .respond({ code: 200, message: 'success' });

            $scope.toggle_light(obj);
        });
    });

    it('shows an error when monitor data loading fails', function() {
        $httpBackend.expectPOST('/dashboard/instant_data_filter?district=ALL&mandal=ALL&gp=ALL&village=ALL&page=0&size=50').respond(500);
        createController();
        $httpBackend.flush();
        expect($scope.errorMessage).toBe('Unable to load monitor data. Please try again.');
        expect($scope.filteredData).toEqual([]);
    });

    it('keeps returned DCUs visible in the filtered list', function() {
        $httpBackend.expectPOST('/dashboard/instant_data_filter?district=ALL&mandal=ALL&gp=ALL&village=ALL&page=0&size=50').respond([{device_name: 'DCU001', dcu_details: {}}]);
        createController();
        $httpBackend.flush();
        expect($scope.filteredData.length).toBe(1);
    });


    describe('$scope.search', function() {
        it('should reset loading flag before loading page', function() {
            createController();

            $scope.loading = true;
            spyOn($scope, 'loadPage').and.callFake(function() {
                expect($scope.loading).toBe(false);
            });

            $scope.searchFish = 'DCU-123';
            $scope.search();

            expect($scope.loadPage).toHaveBeenCalledWith(0);
        });
    });
});
